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
public class AnalyticsPrescription {

    @Id
    private UUID prescriptionId;

    private UUID patientId;

    private UUID doctorId;

    private UUID diagnosisId;

    private Integer medicinesCount;

    private LocalDateTime createdAt;
}
