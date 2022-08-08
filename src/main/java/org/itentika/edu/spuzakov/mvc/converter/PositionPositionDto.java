package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.PositionDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Position;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class PositionPositionDto implements Converter<Position, PositionDto> {
    @Override
    public PositionDto convert(Position source) {
        return PositionDto.builder()
                .id(source.getId())
                .title(source.getTitle())
                .build();
    }
}
