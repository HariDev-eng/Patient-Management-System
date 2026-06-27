package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.entity.AnalyticsPrescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalyticsPrescriptionRepository
        extends JpaRepository<AnalyticsPrescription, UUID> {

    List<AnalyticsPrescription> findByPatientId(UUID patientId);

    List<AnalyticsPrescription> findByDoctorId(UUID doctorId);
}