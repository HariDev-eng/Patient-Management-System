package com.pm.appointmentservice.dto;

import com.pm.appointmentservice.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

    private UUID appointmentId;

    private UUID patientId;

    private UUID doctorId;

    private LocalDateTime appointmentDateTime;

    private AppointmentStatus status;

    private String reason;

    private String notes;

    private LocalDateTime createdAt;
}
