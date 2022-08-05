package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.exception.ConversionStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Client;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ConversionService conversionService;
    private final ClientService clientService;

    private final StaffService staffService;

    public Page<OrderDto> getAllUnfinishedPaginated(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByEndDateOrderByBeginDate(null, pageable);
        List<OrderDto> orderDtoList = orderPage.getContent().stream().map(o -> conversionService.convert(o, OrderDto.class)).toList();
        return new PageImpl<>(orderDtoList, pageable, orderPage.getTotalElements());
    }

    public OrderDto create(String adminName, OrderDto order) {
        Staff admin = staffService.findByNameAndPositionTitle(adminName, "Администратор");
        Order orderForCreate = conversionService.convert(order, Order.class);
        Client approvedClient;
        if (orderForCreate != null) {
            approvedClient = clientService.getApprovedClient(orderForCreate.getClient());
        } else {
            throw new ConversionStoException("Can't convert OrderDto to Order");
        }
        orderForCreate.setAdministrator(admin);
        orderForCreate.setClient(approvedClient);
        Order createdOrder = orderRepository.save(orderForCreate);
        return conversionService.convert(createdOrder, OrderDto.class);

    }
}
