package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderStatusDto;
import org.itentika.edu.spuzakov.mvc.exception.InvalidInputStoException;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderStatusRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderStatus;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Status;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class OrderStatusService {
    private final OrderStatusRepository orderStatusRepository;
    private final ConversionService conversionService;

    private OrderStatus constructStatus(Status status, String comment) {
        return OrderStatus.builder()
                .status(status)
                .comment(comment)
                .build();
    }

    public OrderStatusDto getLastStatus(Long orderId) {
        OrderStatus status = orderStatusRepository.findFirstByOrder_IdOrderByCreateDateDesc(orderId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Status for Order with Id %s not found", orderId));
        });
        return conversionService.convert(status, OrderStatusDto.class);
    }

    public OrderStatus constructNewStatus() {
        return constructStatus(Status.NEW, "This is a new order");
    }

    public OrderStatus constructAcceptedStatus() {
        return constructStatus(Status.ACCEPTED, "Order was accepted");
    }

    public OrderStatus constructCustomStatus(String status, String comment) {
        Status validatedStatus;
        try {
            validatedStatus = Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputStoException(String.format("Wrong Order status type with name %s.", status));
        }
        return constructStatus(validatedStatus, comment);
    }

}
