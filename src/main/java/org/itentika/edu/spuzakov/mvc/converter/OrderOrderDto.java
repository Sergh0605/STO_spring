package org.itentika.edu.spuzakov.mvc.converter;

import lombok.NoArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.dto.StaffDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderStatus;
import org.springframework.core.convert.converter.Converter;

import java.util.Comparator;
import java.util.stream.Collectors;

@NoArgsConstructor
public class OrderOrderDto implements Converter<Order, OrderDto> {
    private final OrderStatusOrderStatusDto orderStatusConverter = new OrderStatusOrderStatusDto();
    private final OrderItemOrderItemDto orderItemConverter = new OrderItemOrderItemDto();

    @Override
    public OrderDto convert(Order source) {
        StaffDto master = null;
        if (source.getMaster() != null) {
            master = new StaffStaffDto().convert(source.getMaster());
        }
        return OrderDto.builder()
                .id(source.getId())
                .reason(source.getReason())
                .beginDate(source.getBeginDate())
                .endDate(source.getEndDate())
                .comment(source.getComment())
                .orderItem(source.getOrderItem().stream()
                        .map(orderItemConverter::convert).collect(Collectors.toList()))
                .orderHistory(source.getOrderHistory().stream()
                        .sorted(Comparator.comparing(OrderStatus::getCreateDate))
                        .map(orderStatusConverter::convert).collect(Collectors.toList()))
                .client(new ClientClientDto().convert(source.getClient()))
                .master(master)
                .administrator(new StaffStaffDto().convert(source.getAdministrator()))
                .build();
    }
}
