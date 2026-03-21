package com.example.paymentservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping("/process")
    public Boolean processPayment(@RequestParam Long orderId, @RequestParam Double amount) {
        // Logic to process payment with external providers...
        // For demonstration, let's assume payment is always successful.
        return true;
    }
}
