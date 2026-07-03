package com.pm.doctorsvc.kafka;

import com.pm.doctorsvc.model.Doctor;
import events.DoctorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsDoctorProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishDoctorCreated(Doctor doctor) {
        publish(doctor, "DOCTOR_CREATED");
    }

    public void publishDoctorUpdated(Doctor doctor) {
        publish(doctor, "DOCTOR_UPDATED");
    }

    public void publishAvailabilityChanged(Doctor doctor) {
        publish(doctor, "DOCTOR_AVAILABILITY_CHANGED");
    }


    private void publish(
            Doctor doctor,
            String eventType) {

        DoctorEvent event =
                DoctorEvent.newBuilder()
                        .setDoctorId(doctor.getDoctorId().toString())
                        .setFirstName(doctor.getFirstName())
                        .setLastName(doctor.getLastName())
                        .setEmail(doctor.getEmail())
                        .setSpecialization(doctor.getSpecialization().name())
                        .setAvailabilityStatus(doctor.getAvailabilityStatus().name())
                        .setEventType(eventType)
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        kafkaTemplate.send(
                "doctor-events",
                event.getDoctorId(),
                event.toByteArray()
        );
    }
    private void publishDeleted(String doctorId) {

        DoctorEvent event =
                DoctorEvent.newBuilder()
                        .setDoctorId(doctorId)
                        .setEventType("DOCTOR_DELETED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        kafkaTemplate.send(
                "doctor-events",
                doctorId,
                event.toByteArray()
        );
    }

    public void publishDoctorDeleted(String doctorId) {
        publishDeleted(doctorId);
    }
}