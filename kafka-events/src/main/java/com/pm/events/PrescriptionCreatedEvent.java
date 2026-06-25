package com.pm.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionCreatedEvent {

    private UUID prescriptionId;

    private UUID patientId;

    private UUID doctorId;

    private UUID diagnosisId;

    private Integer medicinesCount;

    private String eventType;

    private LocalDateTime occurredAt;
}