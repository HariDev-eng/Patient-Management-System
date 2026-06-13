package com.pm.appointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {

    private UUID patientId;

    private UUID doctorId;

    private LocalDate appointmentDateTime;

    private String reason;

    private String notes;
}