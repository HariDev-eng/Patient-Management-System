package com.pm.appointmentservice.kafka;

import events.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishAppointmentCreated(AppointmentEvent event) {
        publish(event);
    }

    public void publishAppointmentUpdated(AppointmentEvent event) {
        publish(event);
    }

    public void publishAppointmentDeleted(AppointmentEvent event) {
        publish(event);
    }

    private void publish(AppointmentEvent event) {

        kafkaTemplate.send(
                "appointment-events",
                event.getAppointmentId(),
                event.toByteArray()
        );
    }
}