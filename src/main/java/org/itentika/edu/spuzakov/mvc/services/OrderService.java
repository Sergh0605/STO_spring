package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.ExOrderStatusDto;
import org.itentika.edu.spuzakov.mvc.dto.IdDto;
import org.itentika.edu.spuzakov.mvc.dto.ItemsDto;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.exception.ConversionStoException;
import org.itentika.edu.spuzakov.mvc.exception.NotEnoughRightsStoException;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ConversionService conversionService;
    private final ClientService clientService;
    private final StaffService staffService;
    private final OrderStatusService orderStatusService;
    private final PriceItemService priceItemService;
    @Value("#{'${sto.sec.rights.acceptOrder}'.split(',')}")
    private final List<String> acceptOrderRoles;
    @Value("#{'${sto.sec.rights.addOrder}'.split(',')}")
    private final List<String> addOrderRoles;
    private final Integer duplicationDayLimit = 1;

    public Page<OrderDto> getAllUnfinishedPaginated(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByEndDateOrderByBeginDate(null, pageable);
        List<OrderDto> orderDtoList = orderPage.getContent().stream().map(o -> {
            OrderDto convertedOrder = conversionService.convert(o, OrderDto.class);
            if (convertedOrder != null) {
                return convertedOrder;
            } else {
                throw new ConversionStoException("Can't convert Order to OrderDto");
            }
        }).toList();
        return new PageImpl<>(orderDtoList, pageable, orderPage.getTotalElements());
    }

    //а какой уровень изоляции здесь предполагается?
    //Isolation.Default для H2 как и для большинства это ReadCommitted
    @Transactional
    public OrderDto create(String login, OrderDto order) {
        Staff admin = staffService.findByLogin(login);
        if (!addOrderRoles.contains(admin.getRole().name())) {
            throw new NotEnoughRightsStoException(String.format("User with login %s don't have rights to create order.", login));
        }
        Order orderForCreate;
        try {
            orderForCreate = conversionService.convert(order, Order.class);
            orderForCreate.setOrderHistory(new HashSet<>());
            orderForCreate.setOrderItem(new HashSet<>());
        } catch (Exception e) {
            throw new ConversionStoException("Can't convert OrderDto to Order", e);
        }
        Client approvedClient;
        if (orderForCreate.getClient().getId() == null) {
            approvedClient = clientService.create(orderForCreate.getClient());
        } else {
            List<Order> duplicatedOrders = getDuplicateOrders(orderForCreate);
            if (duplicatedOrders.size() == 0) {
                approvedClient = clientService.getById(orderForCreate.getClient().getId());
            } else {
                //если в БД есть дублирующий заказ - возвращаем его
                return conversionService.convert(duplicatedOrders.get(0), OrderDto.class);
            }
        }
        orderForCreate.setId(null);
        orderForCreate.setAdministrator(admin);
        orderForCreate.setClient(approvedClient);
        orderForCreate.addStatus(orderStatusService.constructNewStatus());
        Order savedOrder = orderRepository.save(orderForCreate);
        return conversionService.convert(savedOrder, OrderDto.class);

    }

    @Transactional
    public OrderDto acceptOrder(Long orderId, IdDto idForAcceptStatus) {
        Order orderForAccept = getOrder(orderId);
        Staff master = staffService.findById(idForAcceptStatus.getId());
        if (!acceptOrderRoles.contains(master.getRole().name())) {
            throw new NotEnoughRightsStoException(String.format("User with login %s don't have rights to create order.", master.getLogin()));
        }
        orderForAccept.setMaster(master);
        orderForAccept.addStatus(orderStatusService.constructAcceptedStatus());
        Order savedOrder = orderRepository.save(orderForAccept);
        return conversionService.convert(savedOrder, OrderDto.class);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Order with Id %s not found", orderId));
        });
    }

    public OrderDto getOrderForController(Long orderId) {
        return conversionService.convert(getOrder(orderId), OrderDto.class);
    }

    @Transactional
    public OrderDto addItems(Long orderId, ItemsDto itemsDto) {
        Order order = getOrder(orderId);
        List<OrderItem> items = new ArrayList<>(itemsDto.getItems().stream().map(i -> conversionService.convert(i, OrderItem.class)).toList());
        items.forEach(i -> {
            i.setId(null);
            i.setPriceItem(priceItemService.getPriceItem(i.getPriceItem().getId()));
            ////а если 2 раза подряд сервис создания заказа вызвать? у нас задублируются items в базе?
            //Items с одинаковым PriceItemId просуммируются по количеству и сумме;
            addItemWhithoutDuplication(order, i);
        });
        Order savedOrder = orderRepository.save(order);
        return conversionService.convert(savedOrder, OrderDto.class);
    }

    @Transactional
    public OrderDto addStatus(Long orderId, ExOrderStatusDto statusDto) {
        Order order = getOrder(orderId);
        OrderStatus status = orderStatusService.constructCustomStatus(statusDto.getStatus(), statusDto.getComment());
        if (status.getStatus().equals(Status.DONE)) {
            order.setEndDate(LocalDateTime.now());
        }
        order.addStatus(status);
        Order savedOrder = orderRepository.save(order);
        return conversionService.convert(savedOrder, OrderDto.class);
    }

    //а какой уровень изоляции здесь предполагается?
    //Isolation.Default для H2 как и для большинства это ReadCommitted
    @Transactional
    public OrderDto update(OrderDto orderDto) {
        Order orderForUpdate;
        try {
            orderForUpdate = conversionService.convert(orderDto, Order.class);
        } catch (Exception e) {
            throw new ConversionStoException("Can't convert OrderDto to Order", e);
        }
        Client approvedClient;
        if (orderForUpdate.getClient().getId() == null) {
            approvedClient = clientService.create(orderForUpdate.getClient());
        } else {
            approvedClient = clientService.update(orderForUpdate.getClient());
        }
        Order order = getOrder(orderDto.getId());
        order.setComment(orderForUpdate.getComment());
        order.setReason(orderForUpdate.getReason());
        order.setClient(approvedClient);
        Order savedOrder = orderRepository.save(order);
        return conversionService.convert(savedOrder, OrderDto.class);
    }

    private List<Order> getDuplicateOrders(Order order) {
        //заказы с одинаковыми reason, client_id, созданные в течение суток считаются дубликатами
        LocalDateTime duplicationTimeLimit = LocalDateTime.now().minusDays(duplicationDayLimit);
        return orderRepository.getAllByReasonAndClient_IdAndBeginDateAfterOrderByBeginDate(order.getReason(), order.getClient().getId(), duplicationTimeLimit);
    }

    private void addItemWhithoutDuplication(Order order, OrderItem item) {
        boolean duplicatesFound = false;
        for (OrderItem currentItem : order.getOrderItem()) {
            if (Objects.equals(currentItem.getPriceItem().getId(), item.getPriceItem().getId())) {
                duplicatesFound = true;
                currentItem.setCost(currentItem.getCost() + item.getCost());
                currentItem.setQuantity(currentItem.getQuantity() + item.getQuantity());
            }
        }
        if (!duplicatesFound) {
            order.addItem(item);
        }
    }
}
