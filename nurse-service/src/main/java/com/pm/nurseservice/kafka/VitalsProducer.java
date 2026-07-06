package com.pm.nurseservice.kafka;

import events.VitalsRecordedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VitalsProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishVitalsRecorded(VitalsRecordedEvent event) {

        kafkaTemplate.send(
                "vitals-events",
                event.getVitalId(),
                event.toByteArray()
        );
    }
}