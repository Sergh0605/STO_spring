package org.itentika.edu.spuzakov.mvc.persistence.dao;

import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByNameAndPositionTitle(String name, String positionTitle);
}
