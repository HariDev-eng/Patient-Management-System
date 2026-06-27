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
public class AnalyticsDoctor {

    @Id
    private UUID doctorId;

    private String specialization;

    private String availabilityStatus;

    private LocalDateTime createdAt;
}