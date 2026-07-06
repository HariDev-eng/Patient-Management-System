package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.PrescriptionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrescriptionConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "prescription-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        PrescriptionEvent event =
                PrescriptionEvent.parseFrom(payload);

        switch (event.getEventType()) {

            case "PRESCRIPTION_CREATED" ->
                    analyticsService.savePrescription(event);

            case "PRESCRIPTION_UPDATED" ->
                    analyticsService.updatePrescription(event);

            case "PRESCRIPTION_DELETED" ->
                    analyticsService.deletePrescription(event);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event type: "
                                    + event.getEventType());
        }
    }
}