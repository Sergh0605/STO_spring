package org.itentika.edu.spuzakov.mvc.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PriceItemDto {
    @NonNull
    private Long id;
    private String item;
    private Integer price;
    private UnitDto unit;
}
