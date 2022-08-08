package org.itentika.edu.spuzakov.mvc.converter;

import lombok.NoArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.AcceptedOrderDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderStatus;
import org.springframework.core.convert.converter.Converter;

import java.util.Comparator;
import java.util.stream.Collectors;

@NoArgsConstructor
public class OrderAcceptedOrderDto implements Converter<Order, AcceptedOrderDto> {
    private final OrderStatusOrderStatusDto orderStatusConverter = new OrderStatusOrderStatusDto();
    private final OrderItemOrderItemDto orderItemConverter = new OrderItemOrderItemDto();

    @Override
    public AcceptedOrderDto convert(Order source) {
        return AcceptedOrderDto.builder()
                .id(source.getId())
                .reason(source.getReason())
                .beginDate(source.getBeginDate())
                .endDate(source.getEndDate())
                .comment(source.getComment())
                .client(new ClientClientDto().convert(source.getClient()))
                .orderItem(source.getOrderItem().stream()
                        .map(orderItemConverter::convert).collect(Collectors.toList()))
                .orderHistory(source.getOrderHistory().stream()
                        .sorted(Comparator.comparing(OrderStatus::getCreateDate))
                        .map(orderStatusConverter::convert).collect(Collectors.toList()))
                .master(new StaffStaffDto().convert(source.getMaster()))
                .administrator(new StaffStaffDto().convert(source.getAdministrator()))
                .build();
    }
}
