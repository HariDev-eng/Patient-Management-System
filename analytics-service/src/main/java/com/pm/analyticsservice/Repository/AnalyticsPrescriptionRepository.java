package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsPrescriptionRepository
        extends JpaRepository<AnalyticsPrescription, UUID> {

}