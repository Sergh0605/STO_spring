package org.itentika.edu.spuzakov.mvc.converter;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.StaffDto;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.core.convert.converter.Converter;

@AllArgsConstructor
public class StaffStaffDto implements Converter<Staff, StaffDto> {
    @Override
    public StaffDto convert(Staff source) {
        return StaffDto.builder()
                .id(source.getId())
                .name(source.getName())
                .phone(source.getPhone())
                .position(new PositionPositionDto().convert(source.getPosition()))
                .build();
    }
}
