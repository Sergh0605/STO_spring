package org.itentika.edu.spuzakov.mvc.controllers;

import lombok.AllArgsConstructor;
import org.itentika.edu.spuzakov.mvc.dto.ExOrderStatusDto;
import org.itentika.edu.spuzakov.mvc.dto.IdDto;
import org.itentika.edu.spuzakov.mvc.dto.ItemsDto;
import org.itentika.edu.spuzakov.mvc.dto.OrderDto;
import org.itentika.edu.spuzakov.mvc.services.OrderService;
import org.itentika.edu.spuzakov.mvc.services.OrderStatusService;
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
    private final OrderStatusService orderStatusService;

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

    @PostMapping(path = "/{orderId}/accept")
    public ResponseEntity<?> acceptOrder(@PathVariable Long orderId, @RequestBody IdDto masterId) {
        return ResponseEntity.ok(orderService.acceptOrder(orderId, masterId));
    }

    @PostMapping(path = "/{orderId}/items")
    public ResponseEntity<?> addItems(@PathVariable Long orderId, @RequestBody ItemsDto items) {
        return ResponseEntity.ok(orderService.addItems(orderId, items));
    }

    @PostMapping(path = "/{orderId}/status")
    public ResponseEntity<?> addStatus(@PathVariable Long orderId, @RequestBody ExOrderStatusDto statusDto) {
        return ResponseEntity.ok(orderService.addStatus(orderId, statusDto));
    }

    @GetMapping(path = "/{orderId}/status")
    public ResponseEntity<?> getStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderStatusService.getLastStatus(orderId));
    }

    @PutMapping
    public ResponseEntity<?> editOrder(
            @RequestBody OrderDto order) {
        return ResponseEntity.ok(orderService.update(order));
    }

}
