package com.example.saga.inventory;

import com.example.saga.common.dto.InventoryEvent;
import com.example.saga.common.dto.InventoryRequestDto;
import com.example.saga.common.dto.InventoryStatus;
import com.example.saga.common.dto.OrderEvent;
import com.example.saga.common.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    // Map to track which product was deducted for which order
    private final ConcurrentHashMap<UUID, Integer> orderProductMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order-event", groupId = "inventory-group")
    @Transactional
    public void consumeOrderEvent(OrderEvent orderEvent) {
        if (OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())) {
            reserveInventory(orderEvent);
        } else if (OrderStatus.ORDER_CANCELLED.equals(orderEvent.getOrderStatus())) {
            releaseInventory(orderEvent);
        }
    }

    private void reserveInventory(OrderEvent orderEvent) {
        Integer productId = orderEvent.getOrderRequestDto().getProductId();
        UUID orderId = orderEvent.getOrderRequestDto().getOrderId();

        InventoryRequestDto inventoryRequestDto = InventoryRequestDto.builder()
                .orderId(orderId)
                .productId(productId)
                .userId(orderEvent.getOrderRequestDto().getUserId())
                .build();

        InventoryStatus status;
        Inventory inventory = inventoryRepository.findById(productId).orElse(null);
        if (inventory != null && inventory.getAvailableQuantity() > 0) {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - 1);
            inventoryRepository.save(inventory);
            orderProductMap.put(orderId, productId);
            status = InventoryStatus.STOCK_RESERVED;
        } else {
            status = InventoryStatus.STOCK_OUT;
        }

        InventoryEvent inventoryEvent = new InventoryEvent(inventoryRequestDto, status);
        kafkaTemplate.send("inventory-event", inventoryEvent);
    }

    private void releaseInventory(OrderEvent orderEvent) {
        UUID orderId = orderEvent.getOrderRequestDto().getOrderId();
        Integer productId = orderProductMap.remove(orderId);

        if (productId != null) {
            inventoryRepository.findById(productId).ifPresent(inventory -> {
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() + 1);
                inventoryRepository.save(inventory);

                InventoryEvent releaseEvent = new InventoryEvent(
                        InventoryRequestDto.builder().orderId(orderId).productId(productId).build(),
                        InventoryStatus.STOCK_RELEASED
                );
                kafkaTemplate.send("inventory-event", releaseEvent);
            });
        }
    }
}
