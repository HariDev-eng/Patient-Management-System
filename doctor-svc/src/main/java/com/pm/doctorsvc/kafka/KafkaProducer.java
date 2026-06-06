package com.pm.doctorsvc.kafka;


import com.pm.doctorsvc.model.Doctor;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import doctor.events.DoctorEvent;

@Service
@AllArgsConstructor
public class KafkaProducer {

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
                        .setDoctorId(
                                doctor.getDoctorId().toString())
                        .setFirstName(
                                doctor.getFirstName())
                        .setLastName(
                                doctor.getLastName())
                        .setEmail(
                                doctor.getEmail())
                        .setSpecialization(
                                doctor.getSpecialization().name())
                        .setAvailabilityStatus(
                                doctor.getAvailabilityStatus().name())
                        .setEventType(
                                eventType)
                        .build();

        kafkaTemplate.send(
                "doctor-events",
                event.toByteArray());
    }
}
