package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderItemDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderItem;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class OrderItemDtoOrderItem implements Converter<OrderItemDto, OrderItem> {
    @Override
    public OrderItem convert(OrderItemDto source) {
        return OrderItem.builder()
                .id(source.getId())
                .quantity(source.getQuantity())
                .cost(source.getCost())
                .priceItem(new PriceItemDtoPriceItem().convert(source.getPriceItem()))
                .build();
    }
}
