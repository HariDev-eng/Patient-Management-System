package com.pm.billingservice.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingRequestDTO {

    private UUID patientId;

    private UUID appointmentId;

    private Double amount;

    private String paymentMethod;
}