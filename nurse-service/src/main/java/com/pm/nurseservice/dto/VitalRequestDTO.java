package com.pm.nurseservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VitalRequestDTO {

    private UUID patientId;

    private UUID nurseId;

    private Double temperature;

    private Integer heartRate;

    private Integer systolicBP;

    private Integer diastolicBP;

    private Double weight;

    private Double height;

    private Integer oxygenSaturation;
}
