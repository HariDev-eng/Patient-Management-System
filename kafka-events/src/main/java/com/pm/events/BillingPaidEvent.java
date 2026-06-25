package com.pm.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingPaidEvent {

    private UUID billId;

    private UUID patientId;

    private Double amount;

    private String paymentStatus;

    private String eventType;

    private LocalDateTime occurredAt;
}