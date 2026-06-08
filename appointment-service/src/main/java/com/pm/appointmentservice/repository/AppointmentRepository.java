package com.pm.appointmentservice.repository;

import com.pm.appointmentservice.enums.AppointmentStatus;
import com.pm.appointmentservice.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    boolean existsByEmail(String email);

    List<Appointment> findByPatientId(UUID patientId);

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByStatus(
            AppointmentStatus status);
}
