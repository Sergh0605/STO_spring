package org.itentika.edu.spuzakov.mvc.services;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.itentika.edu.spuzakov.mvc.persistence.dao.PriceItemRepository;
import org.itentika.edu.spuzakov.mvc.persistence.domain.PriceItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PriceItemService {
    private final PriceItemRepository priceItemRepository;

    public PriceItem getPriceItem(Long priceItemId) {
        return priceItemRepository.findById(priceItemId).orElseThrow(() -> {
            throw new NotFoundStoException(String.format("PriceItem with Id %s not found", priceItemId));
        });
    }
}
