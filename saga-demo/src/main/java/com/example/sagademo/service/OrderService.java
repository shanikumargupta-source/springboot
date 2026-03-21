package com.example.sagademo.service;

import com.example.sagademo.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Order placeOrder(String productId, int quantity, double price) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, productId, quantity, price, "PENDING");
        orders.put(orderId, order);

        log.info("Starting Saga for Order: {}", orderId);

        // Saga Step 1: Reserve Inventory
        boolean inventoryReserved = inventoryService.reserveInventory(productId, quantity);
        if (!inventoryReserved) {
            order.setStatus("FAILED_INVENTORY");
            log.error("Saga failed at Inventory Step. Final state: {}", order.getStatus());
            return order;
        }

        // Saga Step 2: Process Payment
        boolean paymentProcessed = paymentService.processPayment(orderId, price * quantity);
        if (!paymentProcessed) {
            // Compensation for Step 1
            inventoryService.releaseInventory(productId, quantity);
            order.setStatus("FAILED_PAYMENT_COMPENSATED");
            log.error("Saga failed at Payment Step. Triggering compensation. Final state: {}", order.getStatus());
            return order;
        }

        // Saga Step 3: Success
        order.setStatus("COMPLETED");
        log.info("Saga completed successfully for order: {}", orderId);
        return order;
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
}
