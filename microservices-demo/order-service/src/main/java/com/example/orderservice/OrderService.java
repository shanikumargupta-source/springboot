package com.example.orderservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Order createOrder(Order order) {
        order.setStatus("CREATED");
        Order savedOrder = orderRepository.save(order);

        // Saga Choreography/Orchestration Simplified Example
        try {
            // Step 1: Check Inventory
            String inventoryUrl = "http://localhost:8083/inventory/check?productId=" + order.getProductId() + "&quantity=" + order.getQuantity();
            Boolean isStockAvailable = restTemplate.getForObject(inventoryUrl, Boolean.class);

            if (isStockAvailable != null && isStockAvailable) {
                // Step 2: Process Payment
                String paymentUrl = "http://localhost:8082/payments/process?orderId=" + savedOrder.getId() + "&amount=" + (order.getPrice() * order.getQuantity());
                Boolean isPaymentSuccess = restTemplate.postForObject(paymentUrl, null, Boolean.class);

                if (isPaymentSuccess != null && isPaymentSuccess) {
                    savedOrder.setStatus("COMPLETED");
                } else {
                    // Compensating Transaction
                    compensate(savedOrder);
                }
            } else {
                savedOrder.setStatus("CANCELLED");
            }
        } catch (Exception e) {
            log.error("Saga failed for order ID: {}", savedOrder.getId(), e);
            compensate(savedOrder);
        }

        return orderRepository.save(savedOrder);
    }

    private void compensate(Order order) {
        log.info("Executing compensating transaction for order ID: {}", order.getId());
        order.setStatus("CANCELLED");
        // Additional logic like releasing inventory if it was reserved...
    }
}
