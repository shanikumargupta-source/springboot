package com.example.saga.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(InventoryRepository repository) {
        return args -> {
            repository.save(Inventory.builder().productId(1).availableQuantity(5).build());
            repository.save(Inventory.builder().productId(2).availableQuantity(0).build());
        };
    }
}
