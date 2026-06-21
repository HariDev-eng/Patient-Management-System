package com.pm.analyticsservice.mapper;

import com.pm.analyticsservice.dto.AnalyticsVitalDTO;
import com.pm.analyticsservice.model.AnalyticsVital;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsVitalMapper {

    public AnalyticsVitalDTO toDTO(
            AnalyticsVital entity) {

        return AnalyticsVitalDTO.builder()
                .vitalId(entity.getVitalId())
                .patientId(entity.getPatientId())
                .nurseId(entity.getNurseId())
                .temperature(entity.getTemperature())
                .heartRate(entity.getHeartRate())
                .respiratoryRate(entity.getRespiratoryRate())
                .oxygenSaturation(entity.getOxygenSaturation())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .bloodPressure(entity.getBloodPressure())
                .recordedAt(entity.getRecordedAt())
                .build();
    }

    public AnalyticsVital toEntity(
            AnalyticsVitalDTO dto) {

        return AnalyticsVital.builder()
                .vitalId(dto.getVitalId())
                .patientId(dto.getPatientId())
                .nurseId(dto.getNurseId())
                .temperature(dto.getTemperature())
                .heartRate(dto.getHeartRate())
                .respiratoryRate(dto.getRespiratoryRate())
                .oxygenSaturation(dto.getOxygenSaturation())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .bloodPressure(dto.getBloodPressure())
                .recordedAt(dto.getRecordedAt())
                .build();
    }
}