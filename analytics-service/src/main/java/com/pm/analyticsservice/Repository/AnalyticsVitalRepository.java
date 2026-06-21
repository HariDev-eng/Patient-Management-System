package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AnalyticsVitalRepository
        extends JpaRepository<AnalyticsVital, UUID> {

    long countByTemperatureGreaterThan(
            Double temperature);

    long countByOxygenSaturationLessThan(
            Integer oxygen);

    @Query("""
            SELECT AVG(a.heartRate)
            FROM AnalyticsVital a
            """)
    Double averageHeartRate();

    @Query("""
            SELECT AVG(a.temperature)
            FROM AnalyticsVital a
            """)
    Double averageTemperature();

    List<AnalyticsVital> findByPatientId(
            UUID patientId);

    long countByPatientId(
            UUID patientId);

    List<AnalyticsVital>
    findTop10ByOrderByRecordedAtDesc();
}