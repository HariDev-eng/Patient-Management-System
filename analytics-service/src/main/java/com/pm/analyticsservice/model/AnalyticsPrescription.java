package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "analytics_prescriptions")
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

    private UUID appointmentId;

    private Integer medicineCount;

    private String eventType;

    private LocalDateTime occurredAt;
}
