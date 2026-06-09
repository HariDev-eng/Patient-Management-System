package com.pm.inventoryservice.mapper;

import com.pm.inventoryservice.dto.InventoryRequestDTO;
import com.pm.inventoryservice.dto.InventoryResponseDTO;
import com.pm.inventoryservice.model.Inventory;

public class InventoryMapper {

    private InventoryMapper() {
    }

    public static Inventory toEntity(
            InventoryRequestDTO dto) {

        return Inventory.builder()
                .itemName(dto.getItemName())
                .itemCode(dto.getItemCode())
                .description(dto.getDescription())
                .quantity(dto.getQuantity())
                .minimumStock(dto.getMinimumStock())
                .unitPrice(dto.getUnitPrice())
                .build();
    }

    public static InventoryResponseDTO toDTO(
            Inventory inventory) {

        return InventoryResponseDTO.builder()
                .inventoryId(inventory.getInventoryId())
                .itemName(inventory.getItemName())
                .itemCode(inventory.getItemCode())
                .description(inventory.getDescription())
                .quantity(inventory.getQuantity())
                .minimumStock(inventory.getMinimumStock())
                .unitPrice(inventory.getUnitPrice())
                .status(inventory.getStatus())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}