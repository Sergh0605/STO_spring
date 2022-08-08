package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.StaffRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StaffService {
    private final StaffRepository staffRepository;
    private final List<String> acceptPositions = List.of("Мастер", "Старший смены", "Слесарь");

    public Staff findByNameAndPositionTitle(String name, String positionTitle) {
        return staffRepository.findByNameAndPositionTitle(name, positionTitle).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Staff with name %s and position %s not found", name, positionTitle));
        });
    }

    public Staff findAcceptorById(Long id) {
        return staffRepository.findByIdAndPositionInList(id, acceptPositions).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("Staff with Id %s and Acceptation rights not found", id));
        });
    }
}
