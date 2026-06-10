package com.pm.billingservice.dto;

import com.pm.billingservice.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingResponseDTO {

    private UUID billId;

    private UUID patientId;

    private UUID appointmentId;

    private Double amount;

    private String paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}