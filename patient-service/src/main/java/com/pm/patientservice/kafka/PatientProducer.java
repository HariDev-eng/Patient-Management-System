package com.pm.patientservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import events.PatientCreatedEvent;

@Service
@RequiredArgsConstructor
public class PatientProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPatientCreated(
            PatientCreatedEvent event) {

        kafkaTemplate.send(
                "patient-events",
                event.getPatientId().toString(),
                event
        );
    }
}