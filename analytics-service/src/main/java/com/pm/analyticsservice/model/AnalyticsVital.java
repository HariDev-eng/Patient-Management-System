package com.pm.analyticsservice.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analytics_vitals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsVital {

    @Id
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