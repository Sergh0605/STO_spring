package org.itentika.edu.spuzakov.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PriceItemDto {
    private Long id;
    private String item;
    private Integer price;
    private UnitDto unit;
}
