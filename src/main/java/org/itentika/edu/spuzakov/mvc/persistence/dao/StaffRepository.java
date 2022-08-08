package org.itentika.edu.spuzakov.mvc.persistence.dao;

import org.itentika.edu.spuzakov.mvc.persistence.domain.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByNameAndPositionTitle(String name, String positionTitle);

    @Query("SELECT s FROM Staff s WHERE s.position.title IN :positionTitles AND s.id = :id")
    Optional<Staff> findByIdAndPositionInList(
            @Param("id") Long id,
            @Param("positionTitles") Collection<String> positionTitles);
}
