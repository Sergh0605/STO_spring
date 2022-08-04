package org.itentika.edu.spuzakov.mvc.persistence.dao;

import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
