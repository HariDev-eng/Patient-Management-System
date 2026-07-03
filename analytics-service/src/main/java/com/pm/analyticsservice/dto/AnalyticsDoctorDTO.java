package com.pm.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDoctorDTO {

    private UUID doctorId;

    private String specialization;

    private String availabilityStatus;

    private String eventType;

    private LocalDateTime occurredAt;
}