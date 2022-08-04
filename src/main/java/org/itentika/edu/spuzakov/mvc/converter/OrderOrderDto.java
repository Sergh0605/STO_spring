package org.itentika.edu.spuzakov.mvc.converter;

import lombok.NoArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Order;
import org.springframework.core.convert.converter.Converter;

@NoArgsConstructor
public class OrderOrderDto implements Converter<Order, OrderDto> {
    @Override
    public OrderDto convert(Order source) {
        return OrderDto.builder()
                .id(source.getId())
                .reason(source.getReason())
                .beginDate(source.getBeginDate())
                .endDate(source.getEndDate())
                .comment(source.getComment())
                .client(new ClientClientDto().convert(source.getClient()))
                .build();
    }
}
