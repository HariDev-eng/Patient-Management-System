package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnalyticsDoctorRepository
        extends JpaRepository<AnalyticsDoctor, UUID> {

    Long countByAvailabilityStatus(String availabilityStatus);

}