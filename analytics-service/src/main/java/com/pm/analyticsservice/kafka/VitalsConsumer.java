package com.pm.analyticsservice.kafka;

import com.pm.analyticsservice.service.AnalyticsService;
import com.pm.events.VitalsRecordedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VitalsConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "vital-records",
            groupId = "analytics-service"
    )
    public void consume(VitalsRecordedEvent event) {

        analyticsService.saveVital(event);

        System.out.println("Saved vital: " + event.getVitalId());
    }
}