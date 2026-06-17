package com.pm.prescriptiosvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PrescriptionResponseDTO {

    private UUID prescriptionId;

    private UUID patientId;

    private UUID doctorId;

    private UUID diagnosisId;

    private UUID appointmentId;

    private LocalDateTime createdAt;

    private List<PrescriptionItemResponseDTO> items;
}