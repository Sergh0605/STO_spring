package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.PriceItemDto;
import org.itentika.edu.spuzakov.mvc.dto.UnitDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.PriceItem;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class PriceItemPriceItemDto implements Converter<PriceItem, PriceItemDto> {
    @Override
    public PriceItemDto convert(PriceItem source) {
        return PriceItemDto.builder()
                .id(source.getId())
                .item(source.getItem())
                .price(source.getPrice())
                .unit(UnitDto.builder()
                        .id(source.getUnit().getId())
                        .name(source.getUnit().getName())
                        .build())
                .build();
    }
}
