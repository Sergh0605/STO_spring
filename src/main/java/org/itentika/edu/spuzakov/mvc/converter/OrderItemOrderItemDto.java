package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderItemDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderItem;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class OrderItemOrderItemDto implements Converter<OrderItem, OrderItemDto> {
    @Override
    public OrderItemDto convert(OrderItem source) {
        return OrderItemDto.builder()
                .id(source.getId())
                .quantity(source.getQuantity())
                .cost(source.getCost())
                .priceItem(new PriceItemPriceItemDto().convert(source.getPriceItem()))
                .build();
    }
}
