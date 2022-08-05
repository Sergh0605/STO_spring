package org.itentika.edu.spuzakov.mvc.controllers;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.services.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/order", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrdersPageable(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size) {
        return ResponseEntity.ok(orderService.getAllUnfinishedPaginated(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<?> newOrder(
            @RequestParam(name = "adminName", defaultValue = "Администраторов") String adminName,
            @RequestBody OrderDto order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(adminName, order));
    }
}
