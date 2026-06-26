package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticPatient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnalyticsPatientRepository
        extends JpaRepository<AnalyticPatient, UUID> {
}