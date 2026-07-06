package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analytics_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticInventory {

    @Id
    private UUID inventoryId;

    private String itemName;

    private String itemCode;

    private Integer quantity;

    private Integer minimumStock;

    private Double unitPrice;

    private String status;

    private String eventType;

    private LocalDateTime occurredAt;
}