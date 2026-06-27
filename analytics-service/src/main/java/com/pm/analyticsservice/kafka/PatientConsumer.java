package com.pm.analyticsservice.kafka;

import com.pm.analyticsservice.service.AnalyticsService;
import com.pm.events.PatientCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "patient-events",
            groupId = "analytics-service"
    )
    public void consume(
            PatientCreatedEvent event) {

        analyticsService.savePatient(event);
    }
}