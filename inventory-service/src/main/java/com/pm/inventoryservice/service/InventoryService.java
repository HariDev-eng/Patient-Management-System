package com.pm.inventoryservice.service;

import com.pm.inventoryservice.dto.InventoryRequestDTO;
import com.pm.inventoryservice.dto.InventoryResponseDTO;
import com.pm.inventoryservice.enums.InventoryStatus;
import com.pm.inventoryservice.mapper.InventoryMapper;
import com.pm.inventoryservice.model.Inventory;
import com.pm.inventoryservice.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponseDTO createItem(
            InventoryRequestDTO dto) {

        Inventory inventory =
                InventoryMapper.toEntity(dto);

        updateInventoryStatus(inventory);

        Inventory saved =
                inventoryRepository.save(inventory);

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
        inventory.setQuantity(dto.getQuantity());
        inventory.setMinimumStock(dto.getMinimumStock());
        inventory.setUnitPrice(dto.getUnitPrice());

        updateInventoryStatus(inventory);

        Inventory updated =
                inventoryRepository.save(inventory);

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
}