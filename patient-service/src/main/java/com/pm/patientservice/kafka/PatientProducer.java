package com.pm.patientservice.kafka;

import events.PatientEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishPatientCreated(PatientEvent event) {
        publish(event);
    }

    public void publishPatientUpdated(PatientEvent event) {
        publish(event);
    }

    public void publishPatientDeleted(PatientEvent event) {
        publish(event);
    }

    private void publish(PatientEvent event) {

        kafkaTemplate.send(
                "patient-events",
                event.getPatientId(),
                event.toByteArray()
        );
    }
}