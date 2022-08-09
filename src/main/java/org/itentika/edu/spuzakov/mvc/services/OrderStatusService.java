package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderStatusDto;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderStatusRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
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

    @Transactional
    public void newStatus(Order order, Status status, String comment) {
        OrderStatus newStatus = OrderStatus.builder()
                .order(order)
                .status(status)
                .comment(comment)
                .build();
        orderStatusRepository.saveAndFlush(newStatus);
    }

    public OrderStatusDto getLastStatus(Long orderId) {
        OrderStatus status = orderStatusRepository.findFirstByOrder_IdOrderByCreateDateDesc(orderId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Status for Order with Id %s not found", orderId));
        });
        return conversionService.convert(status, OrderStatusDto.class);
    }
}
