package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsDiagnosisRepository
        extends JpaRepository<AnalyticsDiagnosis, UUID> {

    List<AnalyticsDiagnosis> findByPatientId(UUID patientId);

    List<AnalyticsDiagnosis> findByDoctorId(UUID doctorId);

    List<AnalyticsDiagnosis> findByFollowUpRequiredTrue();

    List<AnalyticsDiagnosis> findByFollowUpDate(LocalDate followUpDate);

    long countByFollowUpRequiredTrue();
}