package com.pm.diagnosissvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DiagnosisResponseDTO {

    private UUID diagnosisId;

    private UUID patientId;

    private UUID doctorId;

    private UUID appointmentId;

    private String symptoms;

    private String diagnosis;

    private String treatmentPlan;

    private String notes;

    private LocalDate followUpDate;

    private LocalDateTime createdAt;
}