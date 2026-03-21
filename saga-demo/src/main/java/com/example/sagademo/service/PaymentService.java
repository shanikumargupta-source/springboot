package com.example.sagademo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentService {
    private final Map<String, Double> payments = new ConcurrentHashMap<>();

    public boolean processPayment(String orderId, double amount) {
        log.info("Processing payment for order {}: ${}", orderId, amount);
        if (amount > 1000) {
            log.warn("Payment failed for order {}: Amount exceeds limit", orderId);
            return false;
        }
        payments.put(orderId, amount);
        log.info("Payment successful for order {}", orderId);
        return true;
    }

    public void refundPayment(String orderId, double amount) {
        log.info("Refunding payment for order {}: ${}", orderId, amount);
        payments.remove(orderId);
    }
}
