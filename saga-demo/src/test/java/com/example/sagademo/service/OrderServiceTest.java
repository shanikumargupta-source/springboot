package com.example.sagademo.service;

import com.example.sagademo.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void testSuccessfulSaga() {
        Order order = orderService.placeOrder("PRODUCT1", 1, 50.0);
        assertEquals("COMPLETED", order.getStatus());
    }

    @Test
    void testFailedInventorySaga() {
        Order order = orderService.placeOrder("PRODUCT2", 100, 10.0);
        assertEquals("FAILED_INVENTORY", order.getStatus());
    }

    @Test
    void testFailedPaymentSagaWithCompensation() {
        Order order = orderService.placeOrder("PRODUCT1", 1, 2000.0);
        assertEquals("FAILED_PAYMENT_COMPENSATED", order.getStatus());
    }
}
