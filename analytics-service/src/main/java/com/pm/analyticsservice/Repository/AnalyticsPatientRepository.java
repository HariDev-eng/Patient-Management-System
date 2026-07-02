package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsPatientRepository
        extends JpaRepository<AnalyticPatient, UUID> {
    List<AnalyticPatient> findByGender(String gender);

    List<AnalyticPatient> findByBloodGroup(String bloodGroup);

    Long countByGender(String gender);
}