package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.StaffRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;

    public Staff findByNameAndPositionTitle(String name, String positionTitle) {
        return staffRepository.findByNameAndPositionTitle(name, positionTitle).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Staff with name %s and position %s not found", name, positionTitle));
        });
    }
}
