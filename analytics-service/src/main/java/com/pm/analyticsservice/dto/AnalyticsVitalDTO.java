package com.pm.analyticsservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsVitalDTO {

    private UUID vitalId;

    private UUID patientId;

    private UUID nurseId;

    private Double temperature;

    private Integer heartRate;

    private Integer respiratoryRate;

    private Integer oxygenSaturation;

    private Double weight;

    private Double height;

    private String bloodPressure;

    private LocalDateTime recordedAt;
}
