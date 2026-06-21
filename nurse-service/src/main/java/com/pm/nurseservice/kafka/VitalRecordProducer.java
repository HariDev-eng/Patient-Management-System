package com.pm.nurseservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.pm.events.VitalsRecordedEvent;

@Service
@RequiredArgsConstructor
public class VitalRecordProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishVitalRecord(
            VitalsRecordedEvent event) {

        kafkaTemplate.send(
                "vital-records",
                event.getPatientId().toString(),
                event
        );
    }
}