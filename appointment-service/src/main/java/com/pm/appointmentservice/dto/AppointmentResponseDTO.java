package com.pm.appointmentservice.dto;

import com.pm.appointmentservice.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
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

    private LocalDate appointmentDateTime;

    private AppointmentStatus status;

    private String reason;

    private String notes;
}
