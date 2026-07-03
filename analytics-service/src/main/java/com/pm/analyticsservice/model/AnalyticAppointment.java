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
public class AnalyticAppointment {

    @Id
    private UUID appointmentId;

    private UUID patientId;

    private UUID doctorId;

    private String status;

    private String reason;

    private LocalDateTime appointmentDateTime;

    private String eventType;

    private LocalDateTime occurredAt;
}