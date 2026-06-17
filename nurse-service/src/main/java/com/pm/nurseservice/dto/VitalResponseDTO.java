package com.pm.nurseservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VitalResponseDTO {

    private UUID vitalId;

    private UUID patientId;

    private UUID nurseId;

    private Double temperature;

    private Integer heartRate;

    private Integer systolicBP;

    private Integer diastolicBP;

    private Double weight;

    private Double height;

    private Integer oxygenSaturation;

    private LocalDateTime recordedAt;
}