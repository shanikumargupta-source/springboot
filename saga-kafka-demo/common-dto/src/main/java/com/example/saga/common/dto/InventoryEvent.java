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
public class InventoryEvent {
    private InventoryRequestDto inventoryRequestDto;
    private InventoryStatus inventoryStatus;
    private LocalDateTime eventDate = LocalDateTime.now();

    public InventoryEvent(InventoryRequestDto inventoryRequestDto, InventoryStatus inventoryStatus) {
        this.inventoryRequestDto = inventoryRequestDto;
        this.inventoryStatus = inventoryStatus;
        this.eventDate = LocalDateTime.now();
    }
}
