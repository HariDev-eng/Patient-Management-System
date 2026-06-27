package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsAppointment {

    @Id
    private UUID appointmentId;

    private UUID patientId;

    private UUID doctorId;

    private String status;

    private LocalDate appointmentDate;
}