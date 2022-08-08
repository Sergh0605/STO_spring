package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.*;
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
    private final OrderItemService orderItemService;

    public Page<OrderDto> getAllUnfinishedPaginated(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByEndDateOrderByBeginDate(null, pageable);
        List<OrderDto> orderDtoList = orderPage.getContent().stream().map(o -> conversionService.convert(o, OrderDto.class)).toList();
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
        orderForCreate.setId(null);
        orderForCreate.setAdministrator(admin);
        orderForCreate.setClient(approvedClient);
        Order createdOrder = orderRepository.save(orderForCreate);
        orderStatusService.newStatus(createdOrder, Status.NEW, "This is a new order");
        return conversionService.convert(orderRepository.findById(createdOrder.getId()), OrderDto.class);

    }

    @Transactional
    public AcceptedOrderDto acceptOrder(Long orderId, IdDto idForAcceptStatus) {
        Order orderForAccept = getOrder(orderId);
        Staff master = staffService.findAcceptorById(idForAcceptStatus.getId());
        orderForAccept.setMaster(master);
        orderStatusService.newStatus(orderForAccept, Status.ACCEPTED, "Order was accepted");
        return conversionService.convert(orderRepository.saveAndFlush(orderForAccept), AcceptedOrderDto.class);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Order with Id %s not found", orderId));
        });
    }

    @Transactional
    public AcceptedOrderDto addItems(Long orderId, ItemsDto itemsDto) {
        Order order = getOrder(orderId);
        List<OrderItem> items = itemsDto.getItems().stream().map(i -> conversionService.convert(i, OrderItem.class)).toList();
        items.forEach(i -> i.setId(null));
        items.forEach(i -> i.setOrder(order));
        orderItemService.addItems(items);
        return conversionService.convert(getOrder(orderId), AcceptedOrderDto.class);
    }

    @Transactional
    public AcceptedOrderDto addStatus(Long orderId, ExOrderStatusDto statusDto) {
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
        orderStatusService.newStatus(order, status, statusDto.getComment());
        return conversionService.convert(getOrder(orderId), AcceptedOrderDto.class);
    }

    @Transactional
    public AcceptedOrderDto update(OrderDto orderDto) {
        Order orderForUpdate = conversionService.convert(orderDto, Order.class);
        Order order = getOrder(orderDto.getId());
        Client approvedClient = clientService.getApprovedClient(orderForUpdate.getClient());
        order.setComment(orderForUpdate.getComment());
        order.setReason(orderForUpdate.getReason());
        order.setClient(approvedClient);
        return conversionService.convert(orderRepository.save(order), AcceptedOrderDto.class);
    }
}
