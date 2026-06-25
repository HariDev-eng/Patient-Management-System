package com.pm.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreatedEvent {

    private UUID patientId;
    private String gender;
    private String bloodGroup;

    private String eventType;
    private LocalDateTime occurredAt;
}