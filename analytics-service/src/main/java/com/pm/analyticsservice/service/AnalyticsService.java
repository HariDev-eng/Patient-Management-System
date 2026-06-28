package com.pm.analyticsservice.service;

import com.pm.analyticsservice.Repository.AnalyticsPatientRepository;
import com.pm.analyticsservice.Repository.AnalyticsVitalRepository;
import com.pm.analyticsservice.dto.AnalyticsSummaryDTO;
import com.pm.analyticsservice.dto.AnalyticsVitalDTO;
import com.pm.analyticsservice.mapper.AnalyticsVitalMapper;
import com.pm.analyticsservice.model.AnalyticPatient;
import com.pm.analyticsservice.model.AnalyticsVital;
import com.pm.events.DoctorCreatedEvent;
import com.pm.events.PatientCreatedEvent;
import com.pm.events.VitalsRecordedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsVitalRepository repository;
    private final AnalyticsPatientRepository patientRepository;
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

    public void saveVital(
            VitalsRecordedEvent event) {

        AnalyticsVital vital =
                AnalyticsVital.builder()
                        .vitalId(event.getVitalId())
                        .patientId(event.getPatientId())
                        .nurseId(event.getNurseId())
                        .temperature(event.getTemperature())
                        .heartRate(event.getHeartRate())
                        .systolicBP(event.getSystolicBP())
                        .diastolicBP(event.getDiastolicBP())
                        .weight(event.getWeight())
                        .height(event.getHeight())
                        .oxygenSaturation(event.getOxygenSaturation())
                        .recordedAt(event.getRecordedAt())
                        .build();

        repository.save(vital);
    }

    public void savePatient(PatientCreatedEvent event){
        AnalyticPatient patient = AnalyticPatient.builder()
                .patientId(event.getPatientId())
                .gender(event.getGender())
                .bloodGroup(event.getBloodGroup())
                .eventType(event.getEventType())
                .occurredAt(event.getOccurredAt())
                .build();

        patientRepository.save(patient);
    }

    public void saveDoctor(DoctorCreatedEvent doctor){
        Analytic
    }
}
