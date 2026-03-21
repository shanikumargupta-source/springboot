package com.example.saga.payment;

import com.example.saga.common.dto.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "PAYMENTS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransaction {
    @Id
    private UUID orderId;
    private Integer userId;
    private Double amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
