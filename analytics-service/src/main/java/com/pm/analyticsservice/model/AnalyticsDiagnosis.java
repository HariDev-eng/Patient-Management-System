package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDiagnosis {

    @Id
    private UUID diagnosisId;

    private UUID patientId;

    private UUID doctorId;

    private UUID appointmentId;

    private String diagnosis;

    private Boolean followUpRequired;

    private Integer followUpDays;

    private LocalDate followUpDate;

    private String eventType;

    private LocalDateTime occurredAt;
}