package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "appointment-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        AppointmentEvent event = AppointmentEvent.parseFrom(payload);

        switch (event.getEventType()) {

            case "APPOINTMENT_CREATED" ->
                    analyticsService.saveAppointment(event);

            case "APPOINTMENT_UPDATED" ->
                    analyticsService.updateAppointment(event);

            case "APPOINTMENT_DELETED" ->
                    analyticsService.deleteAppointment(event);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown event: " + event.getEventType());
        }
    }
}
