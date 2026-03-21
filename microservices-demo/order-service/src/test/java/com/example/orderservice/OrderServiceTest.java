package com.example.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void createOrder_Success() {
        Order order = new Order();
        order.setProductId("PROD1");
        order.setQuantity(2);
        order.setPrice(100.0);

        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(restTemplate.postForObject(anyString(), eq(null), eq(Boolean.class))).thenReturn(true);

        Order createdOrder = orderService.createOrder(order);

        assertEquals("COMPLETED", createdOrder.getStatus());
    }

    @Test
    void createOrder_InventoryFail() {
        Order order = new Order();
        order.setProductId("PROD1");
        order.setQuantity(2);
        order.setPrice(100.0);

        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(false);

        Order createdOrder = orderService.createOrder(order);

        assertEquals("CANCELLED", createdOrder.getStatus());
    }

    @Test
    void createOrder_PaymentFail() {
        Order order = new Order();
        order.setProductId("PROD1");
        order.setQuantity(2);
        order.setPrice(100.0);

        when(restTemplate.getForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(restTemplate.postForObject(anyString(), eq(null), eq(Boolean.class))).thenReturn(false);

        Order createdOrder = orderService.createOrder(order);

        assertEquals("CANCELLED", createdOrder.getStatus());
    }
}
