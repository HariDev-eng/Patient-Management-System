package com.pm.diagnosissvc.kafka;

import events.DiagnosisEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagnosisProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishDiagnosisCreated(
            DiagnosisEvent event) {
        publish(event);
    }

    public void publishDiagnosisUpdated(
            DiagnosisEvent event) {
        publish(event);
    }

    public void publishDiagnosisDeleted(
            DiagnosisEvent event) {
        publish(event);
    }

    private void publish(
            DiagnosisEvent event) {

        kafkaTemplate.send(
                "diagnosis-events",
                event.getDiagnosisId(),
                event.toByteArray());
    }
}