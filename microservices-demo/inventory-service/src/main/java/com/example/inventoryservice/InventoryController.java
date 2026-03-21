package com.example.inventoryservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @GetMapping("/check")
    public Boolean checkStock(@RequestParam String productId, @RequestParam Integer quantity) {
        // Logic to check stock in database...
        // For demonstration, let's assume stock is always available.
        return true;
    }
}
