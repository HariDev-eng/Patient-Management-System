package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsAppointmentRepository
        extends JpaRepository<AnalyticAppointment, UUID> {

    List<AnalyticAppointment> findByPatientId(UUID patientId);

    List<AnalyticAppointment> findByDoctorId(UUID doctorId);

    List<AnalyticAppointment> findByStatus(String status);

    Long countByStatus(String status);
}