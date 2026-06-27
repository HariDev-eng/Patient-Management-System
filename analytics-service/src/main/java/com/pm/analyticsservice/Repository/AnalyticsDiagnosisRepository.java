package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.entity.AnalyticsDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalyticsDiagnosisRepository
        extends JpaRepository<AnalyticsDiagnosis, UUID> {

    List<AnalyticsDiagnosis> findByPatientId(UUID patientId);

    List<AnalyticsDiagnosis> findByDoctorId(UUID doctorId);

    long countByDiagnosis(String diagnosis);
}