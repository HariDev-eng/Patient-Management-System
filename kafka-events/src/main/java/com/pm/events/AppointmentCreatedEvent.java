package com.pm.events;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreatedEvent {

    private UUID appointmentId;

    private UUID patientId;

    private UUID doctorId;

    private String status;

    private LocalDate appointmentDate;

    private String eventType;

    private LocalDateTime occurredAt;
}