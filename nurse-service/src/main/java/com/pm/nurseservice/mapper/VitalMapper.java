package com.pm.nurseservice.mapper;

import com.pm.nurseservice.dto.VitalRequestDTO;
import com.pm.nurseservice.dto.VitalResponseDTO;
import com.pm.nurseservice.model.VitalRecord;

public class VitalMapper {

    public static VitalRecord toEntity(
            VitalRequestDTO dto) {

        return VitalRecord.builder()
                .patientId(dto.getPatientId())
                .nurseId(dto.getNurseId())
                .temperature(dto.getTemperature())
                .heartRate(dto.getHeartRate())
                .systolicBP(dto.getSystolicBP())
                .diastolicBP(dto.getDiastolicBP())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .oxygenSaturation(dto.getOxygenSaturation())
                .build();
    }

    public static VitalResponseDTO toDTO(
            VitalRecord entity) {

        return VitalResponseDTO.builder()
                .vitalId(entity.getVitalId())
                .patientId(entity.getPatientId())
                .nurseId(entity.getNurseId())
                .temperature(entity.getTemperature())
                .heartRate(entity.getHeartRate())
                .systolicBP(entity.getSystolicBP())
                .diastolicBP(entity.getDiastolicBP())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .oxygenSaturation(entity.getOxygenSaturation())
                .recordedAt(entity.getRecordedAt())
                .build();
    }
}