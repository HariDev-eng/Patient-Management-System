package com.pm.inventoryservice.dto;

import com.pm.inventoryservice.enums.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {

    private UUID inventoryId;

    private String itemName;

    private String itemCode;

    private String description;

    private Integer quantity;

    private Integer minimumStock;

    private Double unitPrice;

    private InventoryStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}