package com.pm.diagnosissvc.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class DiagnosisRequestDTO {

    private UUID patientId;

    private UUID doctorId;

    private UUID appointmentId;

    private String symptoms;

    private String diagnosis;

    private String treatmentPlan;

    private String notes;

    private LocalDate followUpDate;
}