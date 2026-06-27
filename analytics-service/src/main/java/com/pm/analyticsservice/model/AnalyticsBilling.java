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
public class AnalyticsBilling {

    @Id
    private UUID billId;

    private UUID patientId;

    private Double amount;

    private String paymentStatus;

    private LocalDateTime createdAt;
}
