package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnalyticsDoctorRepository
        extends JpaRepository<AnalyticsDoctor, UUID> {
}