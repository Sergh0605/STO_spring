package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.persistence.dao.OrderItemRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final PriceItemService priceItemService;

    @Transactional
    public void addItems(List<OrderItem> items) {
        items.stream()
                .filter(i -> i.getPriceItem().getId() != null)
                .forEach(orderItem -> orderItem.setPriceItem(priceItemService.getPriceItem(orderItem.getPriceItem().getId())));
        orderItemRepository.saveAll(items);
    }
}
