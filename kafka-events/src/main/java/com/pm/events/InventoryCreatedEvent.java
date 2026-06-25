package com.pm.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCreatedEvent {

    private UUID inventoryId;

    private String itemName;

    private Integer quantity;

    private Integer minimumStock;

    private String eventType;

    private LocalDateTime occurredAt;
}