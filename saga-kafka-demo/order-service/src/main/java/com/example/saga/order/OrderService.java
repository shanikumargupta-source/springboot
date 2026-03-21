package com.example.saga.order;

import com.example.saga.common.dto.OrderEvent;
import com.example.saga.common.dto.OrderRequestDto;
import com.example.saga.common.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder order = PurchaseOrder.builder()
                .id(UUID.randomUUID())
                .userId(orderRequestDto.getUserId())
                .productId(orderRequestDto.getProductId())
                .price(orderRequestDto.getAmount())
                .status(OrderStatus.ORDER_CREATED)
                .build();

        orderRepository.save(order);

        orderRequestDto.setOrderId(order.getId());
        OrderEvent orderEvent = new OrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);
        kafkaTemplate.send("order-event", orderEvent);

        return order;
    }
}
