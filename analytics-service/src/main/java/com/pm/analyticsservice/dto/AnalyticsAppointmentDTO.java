package com.pm.analyticsservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsAppointmentDTO {

    private UUID appointmentId;

    private UUID patientId;

    private UUID doctorId;

    private String status;

    private String reason;

    private LocalDateTime appointmentDateTime;

    private String eventType;

    private LocalDateTime occurredAt;
}