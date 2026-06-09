package com.pm.inventoryservice.model;

import com.pm.inventoryservice.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID inventoryId;

    private String itemName;

    private String itemCode;

    private String description;

    private Integer quantity;

    private Integer minimumStock;

    private Double unitPrice;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
