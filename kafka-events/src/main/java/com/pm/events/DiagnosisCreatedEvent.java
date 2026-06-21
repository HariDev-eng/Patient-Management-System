package com.pm.events;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisCreatedEvent {

    private UUID diagnosisId;

    private UUID patientId;

    private UUID doctorId;

    private String diagnosis;

    private String severity;

    private LocalDateTime diagnosedAt;
}