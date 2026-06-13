package com.pm.appointmentservice.kafka;


import appointment.events.AppointmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentEventProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishAppointmentCreated(AppointmentCreatedEvent event){
        kafkaTemplate.send("appointment-create", event.toByteArray());
    }
}
