package com.pm.analyticsservice.model;

import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticPatient {

    @Id
    private UUID patientId;
    private String gender;
    private String bloodGroup;

    private String eventType;
    private LocalDateTime occurredAt;
}