package com.shrishti.ordermatching.controller;

import com.shrishti.ordermatching.dto.BookSnapshotResponse;
import com.shrishti.ordermatching.dto.OrderRequest;
import com.shrishti.ordermatching.dto.OrderResponse;
import com.shrishti.ordermatching.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse submitOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.submitOrder(request);
    }

    @GetMapping("/book/{symbol}")
    public BookSnapshotResponse getBook(@PathVariable String symbol) {
        return orderService.getBookSnapshot(symbol);
    }
}
