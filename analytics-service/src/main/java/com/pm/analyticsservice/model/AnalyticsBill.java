package com.pm.analyticsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analytics_bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsBill {

    @Id
    private UUID billId;

    private UUID patientId;

    private UUID appointmentId;

    private Double amount;

    private String paymentMethod;

    private String paymentStatus;

    private String eventType;

    private LocalDateTime occurredAt;
}