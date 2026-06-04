package com.pm.doctorsvc.dto;

import com.pm.doctorsvc.enums.Specialization;
import lombok.Data;
import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class DoctorRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Specialization is required")
    private Specialization specialization;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "Experience years is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceYears;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Consultation fee must be greater than 0")
    private Double consultationFee;
}