package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaProducer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(
            KafkaTemplate<String, byte[]> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPatientCreatedEvent(Patient patient) {
        publish(patient, "PATIENT_CREATED");
    }

    public void sendPatientUpdatedEvent(Patient patient) {
        publish(patient, "PATIENT_UPDATED");
    }

    public void sendPatientDeactivatedEvent(Patient patient) {
        publish(patient, "PATIENT_DEACTIVATED");
    }

    private void publish(
            Patient patient,
            String eventType) {

        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(
                        patient.getPatientId().toString())
                .setFirstName(
                        patient.getFirstName())
                .setLastName(
                        patient.getLastName())
                .setEmail(
                        patient.getEmail())
                .setPhone(
                        patient.getPhone())
                .setGender(
                        patient.getGender().name())
                .setBloodGroup(
                        patient.getBloodGroup().name())
                .setStatus(
                        patient.getStatus().name())
                .setEventType(
                        eventType)
                .build();

        try {

            kafkaTemplate.send(
                    "patient-events",
                    event.toByteArray());

            log.info(
                    "Published event {} for patient {}",
                    eventType,
                    patient.getPatientId());

        } catch (Exception e) {

            log.error(
                    "Failed to publish event {}",
                    eventType,
                    e);
        }
    }
}