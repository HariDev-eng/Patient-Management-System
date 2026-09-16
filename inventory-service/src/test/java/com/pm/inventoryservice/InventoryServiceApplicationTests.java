package com.pm.inventoryservice;

import com.pm.inventoryservice.dto.ConsumeStockRequestDTO;
import com.pm.inventoryservice.dto.InventoryRequestDTO;
import com.pm.inventoryservice.dto.InventoryResponseDTO;
import com.pm.inventoryservice.dto.RestockRequestDTO;
import com.pm.inventoryservice.enums.InventoryStatus;
import com.pm.inventoryservice.kafka.InventoryProducer;
import com.pm.inventoryservice.model.Inventory;
import com.pm.inventoryservice.repository.InventoryRepository;
import com.pm.inventoryservice.service.InventoryService;
import events.InventoryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceApplicationTests {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryProducer inventoryProducer;

    @InjectMocks
    private InventoryService inventoryService;

    private UUID inventoryId;
    private Inventory inventory;
    private InventoryRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        inventoryId = UUID.randomUUID();

        inventory = Inventory.builder()
                .inventoryId(inventoryId)
                .itemName("Paracetamol")
                .itemCode("MED-001")
                .description("Pain relief medicine")
                .quantity(100)
                .minimumStock(20)
                .unitPrice(10.0)
                .status(InventoryStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = InventoryRequestDTO.builder()
                .itemName("Paracetamol")
                .itemCode("MED-001")
                .description("Pain relief medicine")
                .quantity(100)
                .minimumStock(20)
                .unitPrice(10.0)
                .build();
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void createItem_shouldSaveAndPublishCreatedEvent() {

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponseDTO result =
                inventoryService.createItem(requestDTO);

        assertNotNull(result);
        assertEquals(inventoryId, result.getInventoryId());
        assertEquals("Paracetamol", result.getItemName());
        assertEquals(100, result.getQuantity());
        assertEquals(
                InventoryStatus.IN_STOCK,
                result.getStatus()
        );

        verify(inventoryRepository)
                .save(any(Inventory.class));

        ArgumentCaptor<InventoryEvent> captor =
                ArgumentCaptor.forClass(InventoryEvent.class);

        verify(inventoryProducer)
                .publishInventoryCreated(captor.capture());

        InventoryEvent event = captor.getValue();

        assertEquals(
                inventoryId.toString(),
                event.getInventoryId()
        );
        assertEquals(
                "Paracetamol",
                event.getItemName()
        );
        assertEquals(
                "MED-001",
                event.getItemCode()
        );
        assertEquals(
                100,
                event.getQuantity()
        );
        assertEquals(
                20,
                event.getMinimumStock()
        );
        assertEquals(
                "IN_STOCK",
                event.getStatus()
        );
        assertEquals(
                "INVENTORY_CREATED",
                event.getEventType()
        );
    }

    @Test
    void createItem_shouldSetOutOfStockWhenQuantityIsZero() {

        InventoryRequestDTO request =
                InventoryRequestDTO.builder()
                        .itemName("Paracetamol")
                        .itemCode("MED-001")
                        .description("Pain relief medicine")
                        .quantity(0)
                        .minimumStock(20)
                        .unitPrice(10.0)
                        .build();

        Inventory saved = Inventory.builder()
                .inventoryId(inventoryId)
                .itemName("Paracetamol")
                .itemCode("MED-001")
                .description("Pain relief medicine")
                .quantity(0)
                .minimumStock(20)
                .unitPrice(10.0)
                .status(InventoryStatus.OUT_OF_STOCK)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(saved);

        InventoryResponseDTO result =
                inventoryService.createItem(request);

        assertEquals(
                InventoryStatus.OUT_OF_STOCK,
                result.getStatus()
        );

        verify(inventoryProducer)
                .publishInventoryCreated(any(InventoryEvent.class));
    }

    @Test
    void createItem_shouldSetLowStockWhenQuantityEqualsMinimumStock() {

        InventoryRequestDTO request =
                InventoryRequestDTO.builder()
                        .itemName("Paracetamol")
                        .itemCode("MED-001")
                        .description("Pain relief medicine")
                        .quantity(20)
                        .minimumStock(20)
                        .unitPrice(10.0)
                        .build();

        Inventory saved = Inventory.builder()
                .inventoryId(inventoryId)
                .itemName("Paracetamol")
                .itemCode("MED-001")
                .description("Pain relief medicine")
                .quantity(20)
                .minimumStock(20)
                .unitPrice(10.0)
                .status(InventoryStatus.LOW_STOCK)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(saved);

        InventoryResponseDTO result =
                inventoryService.createItem(request);

        assertEquals(
                InventoryStatus.LOW_STOCK,
                result.getStatus()
        );

        verify(inventoryProducer)
                .publishInventoryCreated(any(InventoryEvent.class));
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAllItems_shouldReturnAllItems() {

        Inventory secondInventory = Inventory.builder()
                .inventoryId(UUID.randomUUID())
                .itemName("Ibuprofen")
                .itemCode("MED-002")
                .description("Pain relief medicine")
                .quantity(50)
                .minimumStock(10)
                .unitPrice(15.0)
                .status(InventoryStatus.IN_STOCK)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(inventoryRepository.findAll())
                .thenReturn(List.of(
                        inventory,
                        secondInventory
                ));

        List<InventoryResponseDTO> result =
                inventoryService.getAllItems();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(
                "Paracetamol",
                result.get(0).getItemName()
        );
        assertEquals(
                "Ibuprofen",
                result.get(1).getItemName()
        );

        verify(inventoryRepository)
                .findAll();
    }

    @Test
    void getAllItems_shouldReturnEmptyListWhenNoItems() {

        when(inventoryRepository.findAll())
                .thenReturn(List.of());

        List<InventoryResponseDTO> result =
                inventoryService.getAllItems();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(inventoryRepository)
                .findAll();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getItemById_shouldReturnItem() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        InventoryResponseDTO result =
                inventoryService.getItemById(inventoryId);

        assertNotNull(result);
        assertEquals(
                inventoryId,
                result.getInventoryId()
        );
        assertEquals(
                "Paracetamol",
                result.getItemName()
        );
        assertEquals(
                100,
                result.getQuantity()
        );

        verify(inventoryRepository)
                .findById(inventoryId);
    }

    @Test
    void getItemById_shouldThrowWhenItemDoesNotExist() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> inventoryService.getItemById(inventoryId)
                );

        assertEquals(
                "Inventory item not found with id: " + inventoryId,
                exception.getMessage()
        );

        verify(inventoryRepository)
                .findById(inventoryId);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void updateItem_shouldUpdateAndPublishEvent() {

        InventoryRequestDTO updateRequest =
                InventoryRequestDTO.builder()
                        .itemName("Updated Paracetamol")
                        .itemCode("MED-UPDATED")
                        .description("Updated description")
                        .quantity(80)
                        .minimumStock(20)
                        .unitPrice(12.0)
                        .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponseDTO result =
                inventoryService.updateItem(
                        inventoryId,
                        updateRequest
                );

        assertNotNull(result);
        assertEquals(
                "Updated Paracetamol",
                result.getItemName()
        );
        assertEquals(
                "MED-UPDATED",
                result.getItemCode()
        );
        assertEquals(
                80,
                result.getQuantity()
        );
        assertEquals(
                12.0,
                result.getUnitPrice()
        );
        assertEquals(
                InventoryStatus.IN_STOCK,
                result.getStatus()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository)
                .save(inventory);

        ArgumentCaptor<InventoryEvent> captor =
                ArgumentCaptor.forClass(InventoryEvent.class);

        verify(inventoryProducer)
                .publishInventoryUpdated(captor.capture());

        assertEquals(
                "INVENTORY_UPDATED",
                captor.getValue().getEventType()
        );
    }

    @Test
    void updateItem_shouldThrowWhenItemDoesNotExist() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.empty());

        InventoryRequestDTO updateRequest =
                InventoryRequestDTO.builder()
                        .itemName("Updated Item")
                        .itemCode("MED-002")
                        .description("Updated")
                        .quantity(50)
                        .minimumStock(10)
                        .unitPrice(20.0)
                        .build();

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> inventoryService.updateItem(
                                inventoryId,
                                updateRequest
                        )
                );

        assertEquals(
                "Inventory item not found with id: " + inventoryId,
                exception.getMessage()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository, never())
                .save(any());

        verify(inventoryProducer, never())
                .publishInventoryUpdated(any());
    }

    @Test
    void updateItem_shouldPublishLowStockWhenStatusChangesToLowStock() {

        inventory.setQuantity(100);
        inventory.setMinimumStock(20);
        inventory.setStatus(InventoryStatus.IN_STOCK);

        InventoryRequestDTO updateRequest =
                InventoryRequestDTO.builder()
                        .itemName("Paracetamol")
                        .itemCode("MED-001")
                        .description("Pain relief medicine")
                        .quantity(20)
                        .minimumStock(20)
                        .unitPrice(10.0)
                        .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        inventoryService.updateItem(
                inventoryId,
                updateRequest
        );

        verify(inventoryProducer)
                .publishInventoryUpdated(
                        any(InventoryEvent.class)
                );

        ArgumentCaptor<InventoryEvent> lowStockCaptor =
                ArgumentCaptor.forClass(InventoryEvent.class);

        verify(inventoryProducer)
                .publishLowStock(lowStockCaptor.capture());

        InventoryEvent event =
                lowStockCaptor.getValue();

        assertEquals(
                "INVENTORY_LOW_STOCK",
                event.getEventType()
        );

        assertEquals(
                "LOW_STOCK",
                event.getStatus()
        );

        assertEquals(
                20,
                event.getQuantity()
        );
    }

    @Test
    void updateItem_shouldNotPublishLowStockWhenAlreadyLowStock() {

        inventory.setQuantity(20);
        inventory.setMinimumStock(20);
        inventory.setStatus(InventoryStatus.LOW_STOCK);

        InventoryRequestDTO updateRequest =
                InventoryRequestDTO.builder()
                        .itemName("Paracetamol")
                        .itemCode("MED-001")
                        .description("Pain relief medicine")
                        .quantity(15)
                        .minimumStock(20)
                        .unitPrice(10.0)
                        .build();

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        inventoryService.updateItem(
                inventoryId,
                updateRequest
        );

        verify(inventoryProducer)
                .publishInventoryUpdated(
                        any(InventoryEvent.class)
                );

        verify(inventoryProducer, never())
                .publishLowStock(any(InventoryEvent.class));
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void deleteItem_shouldPublishEventAndDelete() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        inventoryService.deleteItem(inventoryId);

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository)
                .delete(inventory);

        ArgumentCaptor<InventoryEvent> captor =
                ArgumentCaptor.forClass(InventoryEvent.class);

        verify(inventoryProducer)
                .publishInventoryDeleted(captor.capture());

        InventoryEvent event =
                captor.getValue();

        assertEquals(
                inventoryId.toString(),
                event.getInventoryId()
        );

        assertEquals(
                "INVENTORY_DELETED",
                event.getEventType()
        );
    }

    @Test
    void deleteItem_shouldThrowWhenItemDoesNotExist() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> inventoryService.deleteItem(inventoryId)
                );

        assertEquals(
                "Inventory item not found with id: " + inventoryId,
                exception.getMessage()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository, never())
                .delete(any());

        verify(inventoryProducer, never())
                .publishInventoryDeleted(any());
    }

    // =========================================================
    // SEARCH
    // =========================================================

    @Test
    void searchByName_shouldReturnMatchingItems() {

        when(inventoryRepository
                .findByItemNameContainingIgnoreCase("para"))
                .thenReturn(List.of(inventory));

        List<InventoryResponseDTO> result =
                inventoryService.searchByName("para");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                "Paracetamol",
                result.get(0).getItemName()
        );

        verify(inventoryRepository)
                .findByItemNameContainingIgnoreCase("para");
    }

    // =========================================================
    // LOW STOCK
    // =========================================================

    @Test
    void getLowStockItems_shouldReturnLowStockItems() {

        inventory.setQuantity(20);
        inventory.setMinimumStock(20);
        inventory.setStatus(InventoryStatus.LOW_STOCK);

        when(inventoryRepository
                .findByStatus(InventoryStatus.LOW_STOCK))
                .thenReturn(List.of(inventory));

        List<InventoryResponseDTO> result =
                inventoryService.getLowStockItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                InventoryStatus.LOW_STOCK,
                result.get(0).getStatus()
        );

        verify(inventoryRepository)
                .findByStatus(InventoryStatus.LOW_STOCK);
    }

    // =========================================================
    // CONSUME STOCK
    // =========================================================

    @Test
    void consumeStock_shouldReduceQuantity() {

        inventory.setQuantity(100);
        inventory.setMinimumStock(20);
        inventory.setStatus(InventoryStatus.IN_STOCK);

        ConsumeStockRequestDTO request =
                new ConsumeStockRequestDTO();

        request.setQuantity(30);

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponseDTO result =
                inventoryService.consumeStock(
                        inventoryId,
                        request
                );

        assertEquals(
                70,
                result.getQuantity()
        );

        assertEquals(
                InventoryStatus.IN_STOCK,
                result.getStatus()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryProducer)
                .publishInventoryUpdated(
                        any(InventoryEvent.class)
                );
    }

    @Test
    void consumeStock_shouldThrowWhenInsufficientInventory() {

        inventory.setQuantity(10);

        ConsumeStockRequestDTO request =
                new ConsumeStockRequestDTO();

        request.setQuantity(20);

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> inventoryService.consumeStock(
                                inventoryId,
                                request
                        )
                );

        assertEquals(
                "Insufficient inventory",
                exception.getMessage()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository, never())
                .save(any());

        verify(inventoryProducer, never())
                .publishInventoryUpdated(any());
    }

    @Test
    void consumeStock_shouldPublishLowStockWhenCrossingThreshold() {

        inventory.setQuantity(50);
        inventory.setMinimumStock(20);
        inventory.setStatus(InventoryStatus.IN_STOCK);

        ConsumeStockRequestDTO request =
                new ConsumeStockRequestDTO();

        request.setQuantity(35);

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        inventoryService.consumeStock(
                inventoryId,
                request
        );

        assertEquals(
                15,
                inventory.getQuantity()
        );

        assertEquals(
                InventoryStatus.LOW_STOCK,
                inventory.getStatus()
        );

        verify(inventoryProducer)
                .publishInventoryUpdated(
                        any(InventoryEvent.class)
                );

        ArgumentCaptor<InventoryEvent> captor =
                ArgumentCaptor.forClass(InventoryEvent.class);

        verify(inventoryProducer)
                .publishLowStock(captor.capture());

        InventoryEvent event =
                captor.getValue();

        assertEquals(
                "INVENTORY_LOW_STOCK",
                event.getEventType()
        );

        assertEquals(
                "LOW_STOCK",
                event.getStatus()
        );

        assertEquals(
                15,
                event.getQuantity()
        );
    }

    @Test
    void consumeStock_shouldSetOutOfStockWhenAllStockConsumed() {

        inventory.setQuantity(30);
        inventory.setMinimumStock(10);
        inventory.setStatus(InventoryStatus.IN_STOCK);

        ConsumeStockRequestDTO request =
                new ConsumeStockRequestDTO();

        request.setQuantity(30);

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponseDTO result =
                inventoryService.consumeStock(
                        inventoryId,
                        request
                );

        assertEquals(
                0,
                result.getQuantity()
        );

        assertEquals(
                InventoryStatus.OUT_OF_STOCK,
                result.getStatus()
        );

        verify(inventoryProducer)
                .publishInventoryUpdated(
                        any(InventoryEvent.class)
                );

        verify(inventoryProducer, never())
                .publishLowStock(any(InventoryEvent.class));
    }

    @Test
    void consumeStock_shouldThrowWhenItemDoesNotExist() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.empty());

        ConsumeStockRequestDTO request =
                new ConsumeStockRequestDTO();

        request.setQuantity(10);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> inventoryService.consumeStock(
                                inventoryId,
                                request
                        )
                );

        assertEquals(
                "Inventory item not found",
                exception.getMessage()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository, never())
                .save(any());

        verify(inventoryProducer, never())
                .publishInventoryUpdated(any());
    }

    // =========================================================
    // RESTOCK
    // =========================================================

    @Test
    void restockItem_shouldIncreaseQuantity() {

        inventory.setQuantity(50);
        inventory.setMinimumStock(20);
        inventory.setStatus(InventoryStatus.IN_STOCK);

        RestockRequestDTO request =
                new RestockRequestDTO();

        request.setQuantity(30);

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponseDTO result =
                inventoryService.restockItem(
                        inventoryId,
                        request
                );

        assertEquals(
                80,
                result.getQuantity()
        );

        assertEquals(
                InventoryStatus.IN_STOCK,
                result.getStatus()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository)
                .save(inventory);

        verify(inventoryProducer)
                .publishInventoryUpdated(
                        any(InventoryEvent.class)
                );
    }

    @Test
    void restockItem_shouldThrowWhenItemDoesNotExist() {

        when(inventoryRepository.findById(inventoryId))
                .thenReturn(Optional.empty());

        RestockRequestDTO request =
                new RestockRequestDTO();

        request.setQuantity(30);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> inventoryService.restockItem(
                                inventoryId,
                                request
                        )
                );

        assertEquals(
                "Inventory item not found",
                exception.getMessage()
        );

        verify(inventoryRepository)
                .findById(inventoryId);

        verify(inventoryRepository, never())
                .save(any());

        verify(inventoryProducer, never())
                .publishInventoryUpdated(any());
    }
}