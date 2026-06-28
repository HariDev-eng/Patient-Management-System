package com.pm.appointmentservice.kafka;

import com.pm.events.AppointmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAppointmentCreated(
            AppointmentCreatedEvent event) {

        kafkaTemplate.send(
                "appointment-events",
                event.getAppointmentId().toString(),
                event
        );
    }
}