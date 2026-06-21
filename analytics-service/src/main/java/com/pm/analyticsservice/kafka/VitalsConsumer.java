package com.pm.analyticsservice.kafka;

import com.pm.events.VitalsRecordedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class VitalsConsumer {

    @KafkaListener(
            topics = "vital-records",
            groupId = "analytics-service"
    )
    public void consume(
            VitalsRecordedEvent event) {

        System.out.println(
                "Received Vital Event = " + event
        );
    }
}