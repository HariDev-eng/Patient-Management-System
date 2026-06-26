package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AnalyticsAppointmentRepository
        extends JpaRepository<AnalyticsAppointment, UUID> {

    List<AnalyticsAppointment> findByDoctorId(UUID doctorId);

    List<AnalyticsAppointment> findByPatientId(UUID patientId);

    List<AnalyticsAppointment> findByStatus(String status);

    List<AnalyticsAppointment> findByAppointmentDate(LocalDate appointmentDate);
}