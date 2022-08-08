package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderStatusDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderStatus;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class OrderStatusOrderStatusDto implements Converter<OrderStatus, OrderStatusDto> {
    @Override
    public OrderStatusDto convert(OrderStatus source) {
        return OrderStatusDto.builder()
                .id(source.getId())
                .status(source.getStatus().name())
                .comment(source.getComment())
                .createDate(source.getCreateDate())
                .build();
    }
}
