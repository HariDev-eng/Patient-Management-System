package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.DiagnosisEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagnosisConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "diagnosis-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        DiagnosisEvent event =
                DiagnosisEvent.parseFrom(payload);

        switch (event.getEventType()) {

            case "DIAGNOSIS_CREATED" ->
                    analyticsService.saveDiagnosis(event);

            case "DIAGNOSIS_UPDATED" ->
                    analyticsService.updateDiagnosis(event);

            case "DIAGNOSIS_DELETED" ->
                    analyticsService.deleteDiagnosis(event);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event type: "
                                    + event.getEventType());
        }
    }
}