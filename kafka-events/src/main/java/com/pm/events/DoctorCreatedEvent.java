package com.pm.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorCreatedEvent {

    private UUID doctorId;

    private String specialization;

    private String availabilityStatus;

    private String eventType;

    private LocalDateTime occurredAt;
}