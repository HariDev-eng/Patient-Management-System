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
public class BillCreatedEvent {

    private UUID billId;

    private UUID patientId;

    private BigDecimal amount;

    private LocalDateTime createdAt;
}