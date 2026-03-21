package com.example.sagademo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@Slf4j
public class InventoryService {
    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();

    public InventoryService() {
        inventory.put("PRODUCT1", 100);
        inventory.put("PRODUCT2", 5);
    }

    public boolean reserveInventory(String productId, int quantity) {
        log.info("Reserving inventory for product {}: quantity {}", productId, quantity);
        int available = inventory.getOrDefault(productId, 0);
        if (available < quantity) {
            log.warn("Inventory reservation failed for product {}: Insufficient stock (Available: {})", productId, available);
            return false;
        }
        inventory.put(productId, available - quantity);
        log.info("Inventory reserved successfully for product {}", productId);
        return true;
    }

    public void releaseInventory(String productId, int quantity) {
        log.info("Releasing inventory for product {}: quantity {}", productId, quantity);
        inventory.put(productId, inventory.getOrDefault(productId, 0) + quantity);
    }
}
