package com.pm.analyticsservice.service;

import com.pm.analyticsservice.Repository.AnalyticsVitalRepository;
import com.pm.analyticsservice.dto.AnalyticsSummaryDTO;
import com.pm.analyticsservice.dto.AnalyticsVitalDTO;
import com.pm.analyticsservice.mapper.AnalyticsVitalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsVitalRepository repository;
    private final AnalyticsVitalMapper mapper;

    public List<AnalyticsVitalDTO> getAllVitals() {

        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<AnalyticsVitalDTO> getVitalsByPatient(
            UUID patientId) {

        return repository.findByPatientId(patientId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public AnalyticsSummaryDTO getDashboardSummary() {

        return AnalyticsSummaryDTO.builder()
                .totalVitals(repository.count())
                .averageHeartRate(
                        repository.averageHeartRate()
                )
                .averageTemperature(
                        repository.averageTemperature()
                )
                .highFeverPatients(
                        repository.countByTemperatureGreaterThan(100.4)
                )
                .lowOxygenPatients(
                        repository.countByOxygenSaturationLessThan(95)
                )
                .build();
    }

    public Long getTotalVitals() {
        return repository.count();
    }

    public Double getAverageHeartRate() {
        return repository.averageHeartRate();
    }

    public Double getAverageTemperature() {
        return repository.averageTemperature();
    }

    public Long getHighFeverCount() {
        return repository.countByTemperatureGreaterThan(100.4);
    }

    public Long getLowOxygenCount() {
        return repository.countByOxygenSaturationLessThan(95);
    }
}
