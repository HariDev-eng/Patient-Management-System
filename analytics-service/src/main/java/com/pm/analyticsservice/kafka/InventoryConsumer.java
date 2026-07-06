package com.pm.analyticsservice.kafka;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.InventoryEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InventoryConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        InventoryEvent event =
                InventoryEvent.parseFrom(payload);

        switch (event.getEventType()) {

            case "INVENTORY_CREATED" ->
                    analyticsService.saveInventory(event);

            case "INVENTORY_UPDATED" ->
                    analyticsService.updateInventory(event);

            case "INVENTORY_LOW_STOCK" ->
                    analyticsService.updateInventory(event);

            case "INVENTORY_DELETED" ->
                    analyticsService.deleteInventory(event);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event type: "
                                    + event.getEventType());
        }
    }
}
