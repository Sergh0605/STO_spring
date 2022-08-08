package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class OrderDtoOrder implements Converter<OrderDto, Order> {
    @Override
    public Order convert(OrderDto source) {
        return Order.builder()
                .id(source.getId())
                .reason(source.getReason())
                .comment(source.getComment())
                .client(new ClientDtoClient().convert(source.getClient()))
                .build();
    }
}
