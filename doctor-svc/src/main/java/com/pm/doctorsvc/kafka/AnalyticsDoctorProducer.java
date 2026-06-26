package com.pm.doctorsvc.kafka;

import com.pm.doctorsvc.model.Doctor;
import com.pm.events.DoctorCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsDoctorProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDoctorCreated(Doctor doctor) {

        DoctorCreatedEvent event =
                DoctorCreatedEvent.builder()
                        .doctorId(doctor.getDoctorId())
                        .specialization(doctor.getSpecialization().name())
                        .availabilityStatus(doctor.getAvailabilityStatus().name())
                        .occurredAt(LocalDateTime.now())
                        .build();

        kafkaTemplate.send(
                "doctor-created",
                doctor.getDoctorId().toString(),
                event
        );
    }
}
