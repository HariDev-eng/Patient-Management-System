package com.pm.inventoryservice.controller;

import com.pm.inventoryservice.dto.ConsumeStockRequestDTO;
import com.pm.inventoryservice.dto.InventoryRequestDTO;
import com.pm.inventoryservice.dto.InventoryResponseDTO;
import com.pm.inventoryservice.dto.RestockRequestDTO;
import com.pm.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createItem(
            @Valid @RequestBody InventoryRequestDTO request) {

        return ResponseEntity.ok(
                inventoryService.createItem(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllItems() {

        return ResponseEntity.ok(
                inventoryService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getItemById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                inventoryService.getItemById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryRequestDTO request) {

        return ResponseEntity.ok(
                inventoryService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable UUID id) {

        inventoryService.deleteItem(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryResponseDTO>> searchByName(
            @RequestParam String name) {

        return ResponseEntity.ok(
                inventoryService.searchByName(name));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponseDTO>> getLowStockItems() {

        return ResponseEntity.ok(
                inventoryService.getLowStockItems());
    }

    @PatchMapping("/{id}/consume")
    public ResponseEntity<InventoryResponseDTO> consumeStock(
            @PathVariable UUID id,
            @Valid @RequestBody ConsumeStockRequestDTO request) {

        return ResponseEntity.ok(
                inventoryService.consumeStock(id, request));
    }

    @PatchMapping("/{id}/restock")
    public ResponseEntity<InventoryResponseDTO> restockItem(
            @PathVariable UUID id,
            @Valid @RequestBody RestockRequestDTO request) {

        return ResponseEntity.ok(
                inventoryService.restockItem(id, request));
    }
}
