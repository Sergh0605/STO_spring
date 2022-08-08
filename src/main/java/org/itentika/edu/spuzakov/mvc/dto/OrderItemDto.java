package org.itentika.edu.spuzakov.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItemDto {
    private Long id;
    private Integer quantity;
    private Long cost;
    private PriceItemDto priceItem;
}
