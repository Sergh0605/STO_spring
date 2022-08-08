package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.PriceItemDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.PriceItem;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Unit;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class PriceItemDtoPriceItem implements Converter<PriceItemDto, PriceItem> {
    @Override
    public PriceItem convert(PriceItemDto source) {
        return PriceItem.builder()
                .id(source.getId())
                .item(source.getItem())
                .price(source.getPrice())
                .unit(Unit.builder()
                        .id(source.getUnit().getId())
                        .name(source.getUnit().getName())
                        .build())
                .build();
    }
}
