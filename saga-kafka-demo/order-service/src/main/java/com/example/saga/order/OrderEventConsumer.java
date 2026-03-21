package com.example.saga.order;

import com.example.saga.common.dto.InventoryEvent;
import com.example.saga.common.dto.InventoryStatus;
import com.example.saga.common.dto.OrderEvent;
import com.example.saga.common.dto.OrderRequestDto;
import com.example.saga.common.dto.OrderStatus;
import com.example.saga.common.dto.PaymentEvent;
import com.example.saga.common.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @KafkaListener(topics = "payment-event", groupId = "order-group")
    @Transactional
    public void consumePaymentEvent(PaymentEvent paymentEvent) {
        orderRepository.findById(paymentEvent.getPaymentRequestDto().getOrderId())
                .ifPresent(order -> {
                    if (PaymentStatus.PAYMENT_COMPLETED.equals(paymentEvent.getPaymentStatus())) {
                        order.setPaymentStatus(true);
                    } else if (PaymentStatus.PAYMENT_FAILED.equals(paymentEvent.getPaymentStatus())) {
                        order.setPaymentStatus(false);
                        cancelOrder(order);
                    }
                    updateOrderStatus(order);
                });
    }

    @KafkaListener(topics = "inventory-event", groupId = "order-group")
    @Transactional
    public void consumeInventoryEvent(InventoryEvent inventoryEvent) {
        orderRepository.findById(inventoryEvent.getInventoryRequestDto().getOrderId())
                .ifPresent(order -> {
                    if (InventoryStatus.STOCK_RESERVED.equals(inventoryEvent.getInventoryStatus())) {
                        order.setInventoryStatus(true);
                    } else if (InventoryStatus.STOCK_OUT.equals(inventoryEvent.getInventoryStatus())) {
                        order.setInventoryStatus(false);
                        cancelOrder(order);
                    }
                    updateOrderStatus(order);
                });
    }

    private void updateOrderStatus(PurchaseOrder order) {
        if (Boolean.TRUE.equals(order.getPaymentStatus()) && Boolean.TRUE.equals(order.getInventoryStatus())) {
            order.setStatus(OrderStatus.ORDER_COMPLETED);
            orderRepository.save(order);
        }
    }

    private void cancelOrder(PurchaseOrder order) {
        if (OrderStatus.ORDER_CANCELLED.equals(order.getStatus())) {
            return;
        }
        order.setStatus(OrderStatus.ORDER_CANCELLED);
        orderRepository.save(order);

        // Notify other services to roll back
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .amount(order.getPrice())
                .build();

        OrderEvent rollbackEvent = new OrderEvent(orderRequestDto, OrderStatus.ORDER_CANCELLED);
        kafkaTemplate.send("order-event", rollbackEvent);
    }
}
