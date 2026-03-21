package com.example.saga.payment;

import com.example.saga.common.dto.OrderEvent;
import com.example.saga.common.dto.OrderStatus;
import com.example.saga.common.dto.PaymentEvent;
import com.example.saga.common.dto.PaymentRequestDto;
import com.example.saga.common.dto.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @KafkaListener(topics = "order-event", groupId = "payment-group")
    @Transactional
    public void consumeOrderEvent(OrderEvent orderEvent) {
        if (OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())) {
            processPayment(orderEvent);
        } else if (OrderStatus.ORDER_CANCELLED.equals(orderEvent.getOrderStatus())) {
            refundPayment(orderEvent);
        }
    }

    private void processPayment(OrderEvent orderEvent) {
        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .orderId(orderEvent.getOrderRequestDto().getOrderId())
                .userId(orderEvent.getOrderRequestDto().getUserId())
                .amount(orderEvent.getOrderRequestDto().getAmount())
                .build();

        PaymentStatus status;
        if (paymentRequestDto.getAmount() > 1000) {
            status = PaymentStatus.PAYMENT_FAILED;
        } else {
            status = PaymentStatus.PAYMENT_COMPLETED;
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .orderId(paymentRequestDto.getOrderId())
                    .userId(paymentRequestDto.getUserId())
                    .amount(paymentRequestDto.getAmount())
                    .status(status)
                    .build();
            paymentRepository.save(transaction);
        }

        PaymentEvent paymentEvent = new PaymentEvent(paymentRequestDto, status);
        kafkaTemplate.send("payment-event", paymentEvent);
    }

    private void refundPayment(OrderEvent orderEvent) {
        paymentRepository.findById(orderEvent.getOrderRequestDto().getOrderId())
                .ifPresent(transaction -> {
                    transaction.setStatus(PaymentStatus.PAYMENT_REFUNDED);
                    paymentRepository.save(transaction);

                    PaymentEvent refundEvent = new PaymentEvent(
                            PaymentRequestDto.builder().orderId(transaction.getOrderId()).build(),
                            PaymentStatus.PAYMENT_REFUNDED
                    );
                    kafkaTemplate.send("payment-event", refundEvent);
                });
    }
}
