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
public class PaymentEvent {
    private PaymentRequestDto paymentRequestDto;
    private PaymentStatus paymentStatus;
    private LocalDateTime eventDate = LocalDateTime.now();

    public PaymentEvent(PaymentRequestDto paymentRequestDto, PaymentStatus paymentStatus) {
        this.paymentRequestDto = paymentRequestDto;
        this.paymentStatus = paymentStatus;
        this.eventDate = LocalDateTime.now();
    }
}
