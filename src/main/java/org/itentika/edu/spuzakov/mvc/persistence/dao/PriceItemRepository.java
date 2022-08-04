package org.itentika.edu.spuzakov.mvc.persistence.dao;

import org.itentika.edu.spuzakov.mvc.persistence.domain.PriceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceItemRepository extends JpaRepository<PriceItem, Long> {
}
