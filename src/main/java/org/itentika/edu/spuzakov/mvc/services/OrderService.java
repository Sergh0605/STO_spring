package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.ExOrderStatusDto;
import org.itentika.edu.spuzakov.mvc.dto.IdDto;
import org.itentika.edu.spuzakov.mvc.dto.ItemsDto;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.exception.ConversionStoException;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.*;
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
import java.util.stream.Collectors;

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
    public OrderDto create(String adminName, OrderDto order) {
        Staff admin = staffService.findByNameAndPositionTitle(adminName, "Администратор");
        Order orderForCreate = conversionService.convert(order, Order.class);
        Client approvedClient;
        if (orderForCreate != null) {
            approvedClient = clientService.getApprovedClient(orderForCreate.getClient());
        } else {
            throw new ConversionStoException("Can't convert OrderDto to Order");
        }
        OrderStatus addStatus = OrderStatus.builder()
                .status(Status.NEW)
                .createDate(LocalDateTime.now())
                .comment("This is a new order")
                .order(orderForCreate)
                .build();
        List<OrderStatus> statuses = new ArrayList<>();
        statuses.add(addStatus);
        orderForCreate.setId(null);
        orderForCreate.setAdministrator(admin);
        orderForCreate.setClient(approvedClient);
        orderForCreate.setOrderItem(Collections.emptyList());
        orderForCreate.setOrderHistory(statuses);
        Order createdOrder = orderRepository.save(orderForCreate);
        return conversionService.convert(createdOrder, OrderDto.class);

    }

    @Transactional
    public OrderDto acceptOrder(Long orderId, IdDto idForAcceptStatus) {
        Order orderForAccept = getOrder(orderId);
        Staff master = staffService.findAcceptorById(idForAcceptStatus.getId());
        orderForAccept.setMaster(master);
        orderStatusService.addStatus(orderForAccept, Status.ACCEPTED, "Order was accepted");
        return conversionService.convert(orderRepository.saveAndFlush(orderForAccept), OrderDto.class);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Order with Id %s not found", orderId));
        });
    }

    @Transactional
    public OrderDto addItems(Long orderId, ItemsDto itemsDto) {
        Order order = getOrder(orderId);
        List<OrderItem> items = itemsDto.getItems().stream().map(i -> conversionService.convert(i, OrderItem.class)).collect(Collectors.toList());
        items.forEach(i -> {
            i.setId(null);
            i.setOrder(order);
            i.setPriceItem(priceItemService.getPriceItem(i.getPriceItem().getId()));
        });
        order.setOrderItem(items);
        orderRepository.save(order);
        return conversionService.convert(order, OrderDto.class);
    }

    @Transactional
    public OrderDto addStatus(Long orderId, ExOrderStatusDto statusDto) {
        Order order = getOrder(orderId);
        Status status;
        try {
            status = Status.valueOf(statusDto.getStatus());
            if (status.equals(Status.DONE)) {
                order.setEndDate(LocalDateTime.now());
                orderRepository.save(order);
            }
        } catch (IllegalArgumentException e) {
            throw new NotFoundStoException(String.format("Order status type with name %s not found.", statusDto.getStatus()));
        }
        orderStatusService.addStatus(order, status, statusDto.getComment());
        return conversionService.convert(getOrder(orderId), OrderDto.class);
    }

    @Transactional
    public OrderDto update(OrderDto orderDto) {
        Order orderForUpdate = conversionService.convert(orderDto, Order.class);
        if (orderForUpdate == null) {
            throw new ConversionStoException("Can't convert OrderDto to Order");
        }
        Order order = getOrder(orderDto.getId());
        Client approvedClient = clientService.getApprovedClient(orderForUpdate.getClient());
        order.setComment(orderForUpdate.getComment());
        order.setReason(orderForUpdate.getReason());
        order.setClient(approvedClient);
        return conversionService.convert(orderRepository.save(order), OrderDto.class);
    }
}
