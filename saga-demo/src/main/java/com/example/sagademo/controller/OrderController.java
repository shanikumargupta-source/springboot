package com.example.sagademo.controller;

import com.example.sagademo.model.Order;
import com.example.sagademo.model.OrderRequest;
import com.example.sagademo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/place")
    public Order placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(
            orderRequest.getProductId(),
            orderRequest.getQuantity(),
            orderRequest.getPrice()
        );
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }
}
