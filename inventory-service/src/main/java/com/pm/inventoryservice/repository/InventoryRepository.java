package com.pm.inventoryservice.repository;

import com.pm.inventoryservice.enums.InventoryStatus;
import com.pm.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryRepository
        extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByStatus(
            InventoryStatus status);

    List<Inventory> findByItemNameContainingIgnoreCase(
            String itemName);
}
