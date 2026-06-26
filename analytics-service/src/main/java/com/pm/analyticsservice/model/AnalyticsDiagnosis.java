package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDiagnosis {

    @Id
    private UUID diagnosisId;

    private UUID patientId;

    private UUID doctorId;

    private String diagnosis;

    private LocalDateTime createdAt;
}
