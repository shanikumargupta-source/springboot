package com.example.saga.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    private OrderRequestDto orderRequestDto;
    private OrderStatus orderStatus;
    private LocalDateTime eventDate = LocalDateTime.now();

    public OrderEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
        this.orderRequestDto = orderRequestDto;
        this.orderStatus = orderStatus;
        this.eventDate = LocalDateTime.now();
    }
}
