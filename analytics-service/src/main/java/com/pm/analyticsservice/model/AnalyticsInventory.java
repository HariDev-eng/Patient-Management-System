package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsInventory {

    @Id
    private UUID inventoryId;

    private String itemName;

    private Integer quantity;

    private Integer minimumStock;
}
