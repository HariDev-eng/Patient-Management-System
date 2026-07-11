package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analytics_patient")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticPatient {

    @Id
    private UUID patientId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String gender;

    private String bloodGroup;

    private String eventType;

    private LocalDateTime occurredAt;
}