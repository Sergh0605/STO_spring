package org.itentika.edu.spuzakov.mvc.persistence.dao;

import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    Optional<OrderStatus> findFirstByOrder_IdOrderByCreateDateDesc(Long order_id);
}
