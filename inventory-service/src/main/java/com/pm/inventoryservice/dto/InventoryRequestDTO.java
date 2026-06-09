package com.pm.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDTO {

    private String itemName;

    private String itemCode;

    private String description;

    private Integer quantity;

    private Integer minimumStock;

    private Double unitPrice;
}