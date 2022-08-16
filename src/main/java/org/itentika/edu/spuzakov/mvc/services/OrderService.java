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
import java.util.Collections;
import java.util.List;

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

    @Transactional
    public OrderDto create(String login, OrderDto order) {
        Staff admin = staffService.findByLogin(login);
        if (!addOrderRoles.contains(admin.getRole().name())) {
            throw new NotEnoughRightsStoException(String.format("User with login %s don't have rights to create order.", login));
        }
        Order orderForCreate = conversionService.convert(order, Order.class);
        Client nonApprovedClient;
        if (orderForCreate != null) {
            nonApprovedClient = orderForCreate.getClient();
        } else {
            throw new ConversionStoException("Can't convert OrderDto to Order");
        }
        Client approvedClient;
        if (nonApprovedClient.getId() == null) {
            approvedClient = clientService.create(nonApprovedClient);
        } else {
            approvedClient = clientService.getById(nonApprovedClient.getId());
        }
        List<OrderStatus> statuses = new ArrayList<>();
        statuses.add(orderStatusService.constructNewStatus(orderForCreate));
        orderForCreate.setId(null);
        orderForCreate.setAdministrator(admin);
        orderForCreate.setClient(approvedClient);
        orderForCreate.setOrderItem(Collections.emptyList());
        orderForCreate.setOrderHistory(statuses);
        return conversionService.convert(orderRepository.save(orderForCreate), OrderDto.class);

    }

    @Transactional
    public OrderDto acceptOrder(Long orderId, IdDto idForAcceptStatus) {
        Order orderForAccept = getOrder(orderId);
        Staff master = staffService.findById(idForAcceptStatus.getId());
        if (!acceptOrderRoles.contains(master.getRole().name())) {
            throw new NotEnoughRightsStoException(String.format("User with login %s don't have rights to create order.", master.getLogin()));
        }
        orderForAccept.setMaster(master);
        orderForAccept.getOrderHistory().add(orderStatusService.constructAcceptedStatus(orderForAccept));
        return conversionService.convert(orderRepository.saveAndFlush(orderForAccept), OrderDto.class);
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
            i.setOrder(order);
            i.setPriceItem(priceItemService.getPriceItem(i.getPriceItem().getId()));
        });
        items.addAll(order.getOrderItem());
        order.setOrderItem(items);
        orderRepository.save(order);
        return conversionService.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto addStatus(Long orderId, ExOrderStatusDto statusDto) {
        Order order = getOrder(orderId);
        OrderStatus status = orderStatusService.constructCustomStatus(order, statusDto.getStatus(), statusDto.getComment());
        if (status.getStatus().equals(Status.DONE)) {
            order.setEndDate(LocalDateTime.now());
        }
        order.getOrderHistory().add(status);
        return conversionService.convert(orderRepository.saveAndFlush(order), OrderDto.class);
    }

    @Transactional
    public OrderDto update(OrderDto orderDto) {
        Order orderForUpdate = conversionService.convert(orderDto, Order.class);
        Client nonApprovedClient;
        if (orderForUpdate != null) {
            nonApprovedClient = orderForUpdate.getClient();
        } else {
            throw new ConversionStoException("Can't convert OrderDto to Order");
        }
        Client approvedClient;
        if (nonApprovedClient.getId() == null) {
            approvedClient = clientService.create(nonApprovedClient);
        } else {
            approvedClient = clientService.update(nonApprovedClient);
        }
        Order order = getOrder(orderDto.getId());
        order.setComment(orderForUpdate.getComment());
        order.setReason(orderForUpdate.getReason());
        order.setClient(approvedClient);
        return conversionService.convert(orderRepository.save(order), OrderDto.class);
    }
}
