package org.itentika.edu.spuzakov.mvc.dto;

import lombok.*;

//какие из полей являются обязательными?
//добавил
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItemDto {
    private Long id;
    @NonNull
    private Integer quantity;
    @NonNull
    private Long cost;
    @NonNull
    private PriceItemDto priceItem;
}
