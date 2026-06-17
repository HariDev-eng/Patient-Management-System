package com.pm.prescriptiosvc.dto;

import lombok.Data;

@Data
public class PrescriptionItemRequestDTO {

    private String medicineName;

    private String dosage;

    private String frequency;

    private Integer durationDays;

    private String instructions;
}