package com.pm.inventoryservice.service;

import com.pm.inventoryservice.dto.ConsumeStockRequestDTO;
import com.pm.inventoryservice.dto.InventoryRequestDTO;
import com.pm.inventoryservice.dto.InventoryResponseDTO;
import com.pm.inventoryservice.dto.RestockRequestDTO;
import com.pm.inventoryservice.enums.InventoryStatus;
import com.pm.inventoryservice.kafka.InventoryProducer;
import com.pm.inventoryservice.mapper.InventoryMapper;
import com.pm.inventoryservice.model.Inventory;
import com.pm.inventoryservice.repository.InventoryRepository;
import events.InventoryEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryProducer inventoryProducer;

    public InventoryResponseDTO createItem(
            InventoryRequestDTO dto) {

        Inventory inventory =
                InventoryMapper.toEntity(dto);

        updateInventoryStatus(inventory);

        Inventory saved =
                inventoryRepository.save(inventory);

        InventoryEvent event =
                InventoryEvent.newBuilder()
                        .setInventoryId(saved.getInventoryId().toString())
                        .setItemName(saved.getItemName())
                        .setItemCode(saved.getItemCode())
                        .setQuantity(saved.getQuantity())
                        .setMinimumStock(saved.getMinimumStock())
                        .setUnitPrice(saved.getUnitPrice())
                        .setStatus(saved.getStatus().name())
                        .setEventType("INVENTORY_CREATED")
                        .setOccurredAt(saved.getCreatedAt().toString())
                        .build();

        inventoryProducer.publishInventoryCreated(event);

        return InventoryMapper.toDTO(saved);
    }

    public List<InventoryResponseDTO> getAllItems() {

        return inventoryRepository.findAll()
                .stream()
                .map(InventoryMapper::toDTO)
                .toList();
    }

    public InventoryResponseDTO getItemById(
            UUID inventoryId) {

        Inventory inventory =
                inventoryRepository.findById(inventoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory item not found with id: "
                                                + inventoryId));

        return InventoryMapper.toDTO(inventory);
    }

    public InventoryResponseDTO updateItem(
            UUID inventoryId,
            InventoryRequestDTO dto) {

        Inventory inventory =
                inventoryRepository.findById(inventoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory item not found with id: "
                                                + inventoryId));

        inventory.setItemName(dto.getItemName());
        inventory.setItemCode(dto.getItemCode());
        inventory.setDescription(dto.getDescription());
        InventoryStatus previousStatus = inventory.getStatus();

        inventory.setQuantity(dto.getQuantity());
        inventory.setMinimumStock(dto.getMinimumStock());
        inventory.setUnitPrice(dto.getUnitPrice());

        updateInventoryStatus(inventory);

        Inventory updated =
                inventoryRepository.save(inventory);

        InventoryEvent event =
                InventoryEvent.newBuilder()
                        .setInventoryId(updated.getInventoryId().toString())
                        .setItemName(updated.getItemName())
                        .setItemCode(updated.getItemCode())
                        .setQuantity(updated.getQuantity())
                        .setMinimumStock(updated.getMinimumStock())
                        .setUnitPrice(updated.getUnitPrice())
                        .setStatus(updated.getStatus().name())
                        .setEventType("INVENTORY_UPDATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        inventoryProducer.publishInventoryUpdated(event);

        if (previousStatus != InventoryStatus.LOW_STOCK &&
                updated.getStatus() == InventoryStatus.LOW_STOCK) {

            InventoryEvent lowStockEvent =
                    InventoryEvent.newBuilder()
                            .setInventoryId(updated.getInventoryId().toString())
                            .setItemName(updated.getItemName())
                            .setItemCode(updated.getItemCode())
                            .setQuantity(updated.getQuantity())
                            .setMinimumStock(updated.getMinimumStock())
                            .setUnitPrice(updated.getUnitPrice())
                            .setStatus(updated.getStatus().name())
                            .setEventType("INVENTORY_LOW_STOCK")
                            .setOccurredAt(LocalDateTime.now().toString())
                            .build();

            inventoryProducer.publishLowStock(lowStockEvent);
        }

        return InventoryMapper.toDTO(updated);
    }

    public void deleteItem(
            UUID inventoryId) {

        Inventory inventory =
                inventoryRepository.findById(inventoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory item not found with id: "
                                                + inventoryId));

        InventoryEvent event =
                InventoryEvent.newBuilder()
                        .setInventoryId(inventory.getInventoryId().toString())
                        .setItemName(inventory.getItemName())
                        .setItemCode(inventory.getItemCode())
                        .setQuantity(inventory.getQuantity())
                        .setMinimumStock(inventory.getMinimumStock())
                        .setUnitPrice(inventory.getUnitPrice())
                        .setStatus(inventory.getStatus().name())
                        .setEventType("INVENTORY_DELETED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        inventoryProducer.publishInventoryDeleted(event);

        inventoryRepository.delete(inventory);
    }

    public List<InventoryResponseDTO> searchByName(
            String name) {

        return inventoryRepository
                .findByItemNameContainingIgnoreCase(name)
                .stream()
                .map(InventoryMapper::toDTO)
                .toList();
    }

    public List<InventoryResponseDTO> getLowStockItems() {

        return inventoryRepository
                .findByStatus(InventoryStatus.LOW_STOCK)
                .stream()
                .map(InventoryMapper::toDTO)
                .toList();
    }

    private void updateInventoryStatus(
            Inventory inventory) {

        if (inventory.getQuantity() == 0) {

            inventory.setStatus(
                    InventoryStatus.OUT_OF_STOCK);

        } else if (
                inventory.getQuantity()
                        <= inventory.getMinimumStock()) {

            inventory.setStatus(
                    InventoryStatus.LOW_STOCK);

        } else {

            inventory.setStatus(
                    InventoryStatus.IN_STOCK);
        }
    }

    public InventoryResponseDTO consumeStock(
            UUID inventoryId,
            ConsumeStockRequestDTO request) {

        Inventory inventory =
                inventoryRepository.findById(inventoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory item not found"));

        if (request.getQuantity() > inventory.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient inventory");
        }

        InventoryStatus previousStatus =
                inventory.getStatus();

        inventory.setQuantity(
                inventory.getQuantity() - request.getQuantity());

        updateInventoryStatus(inventory);

        Inventory updated =
                inventoryRepository.save(inventory);

        InventoryEvent updatedEvent =
                InventoryEvent.newBuilder()
                        .setInventoryId(updated.getInventoryId().toString())
                        .setItemName(updated.getItemName())
                        .setItemCode(updated.getItemCode())
                        .setQuantity(updated.getQuantity())
                        .setMinimumStock(updated.getMinimumStock())
                        .setUnitPrice(updated.getUnitPrice())
                        .setStatus(updated.getStatus().name())
                        .setEventType("INVENTORY_UPDATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        inventoryProducer.publishInventoryUpdated(updatedEvent);

        if (previousStatus != InventoryStatus.LOW_STOCK &&
                updated.getStatus() == InventoryStatus.LOW_STOCK) {

            InventoryEvent lowStockEvent =
                    updatedEvent.toBuilder()
                            .setEventType("INVENTORY_LOW_STOCK")
                            .build();

            inventoryProducer.publishLowStock(lowStockEvent);
        }

        return InventoryMapper.toDTO(updated);
    }

    public InventoryResponseDTO restockItem(
            UUID inventoryId,
            RestockRequestDTO request) {

        Inventory inventory =
                inventoryRepository.findById(inventoryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory item not found"));

        inventory.setQuantity(
                inventory.getQuantity() + request.getQuantity());

        updateInventoryStatus(inventory);

        Inventory updated =
                inventoryRepository.save(inventory);

        InventoryEvent event =
                InventoryEvent.newBuilder()
                        .setInventoryId(updated.getInventoryId().toString())
                        .setItemName(updated.getItemName())
                        .setItemCode(updated.getItemCode())
                        .setQuantity(updated.getQuantity())
                        .setMinimumStock(updated.getMinimumStock())
                        .setUnitPrice(updated.getUnitPrice())
                        .setStatus(updated.getStatus().name())
                        .setEventType("INVENTORY_UPDATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        inventoryProducer.publishInventoryUpdated(event);

        return InventoryMapper.toDTO(updated);
    }
}