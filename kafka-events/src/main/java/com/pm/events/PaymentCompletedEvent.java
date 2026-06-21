package com.pm.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    private UUID paymentId;

    private UUID billId;

    private UUID patientId;

    private BigDecimal amount;

    private String paymentMethod;

    private LocalDateTime paidAt;
}
