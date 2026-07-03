package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.DoctorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "doctor-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        DoctorEvent event = DoctorEvent.parseFrom(payload);

        switch (event.getEventType()) {

            case "DOCTOR_CREATED" ->
                    analyticsService.saveDoctor(event);

            case "DOCTOR_UPDATED" ->
                    analyticsService.updateDoctor(event);

            case "DOCTOR_AVAILABILITY_CHANGED" ->
                    analyticsService.updateDoctorAvailability(event);

            case "DOCTOR_DELETED" ->
                    analyticsService.deleteDoctor(event);
        }
    }
}