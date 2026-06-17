package com.pm.prescriptiosvc.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PrescriptionItemResponseDTO {

    private UUID itemId;

    private String medicineName;

    private String dosage;

    private String frequency;

    private Integer durationDays;

    private String instructions;
}