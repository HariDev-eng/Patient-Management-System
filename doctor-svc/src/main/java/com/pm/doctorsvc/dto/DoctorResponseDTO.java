package com.pm.doctorsvc.dto;

import com.pm.doctorsvc.enums.AvailabilityStatus;
import com.pm.doctorsvc.enums.Specialization;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DoctorResponseDTO {

    private UUID doctorId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Specialization specialization;

    private String licenseNumber;

    private Integer experienceYears;

    private Double consultationFee;

    private AvailabilityStatus availabilityStatus;
}
