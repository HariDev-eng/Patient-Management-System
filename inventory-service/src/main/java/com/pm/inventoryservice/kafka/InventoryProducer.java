package com.pm.inventoryservice.kafka;

import events.InventoryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishInventoryCreated(
            InventoryEvent event) {
        publish(event);
    }

    public void publishInventoryUpdated(
            InventoryEvent event) {
        publish(event);
    }

    public void publishLowStock(
            InventoryEvent event) {
        publish(event);
    }

    public void publishInventoryDeleted(
            InventoryEvent event) {
        publish(event);
    }

    private void publish(
            InventoryEvent event) {

        kafkaTemplate.send(
                "inventory-events",
                event.getInventoryId(),
                event.toByteArray());
    }
}