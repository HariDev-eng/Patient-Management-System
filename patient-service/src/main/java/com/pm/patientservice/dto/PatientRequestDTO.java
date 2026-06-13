package com.pm.patientservice.dto;


import com.pm.patientservice.enums.BloodGroup;
import com.pm.patientservice.enums.Gender;
import com.pm.patientservice.enums.PatientStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PatientRequestDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String phone;

    private PatientStatus status;

    @Email
    private String email;

    private String address;

    private BloodGroup bloodGroup;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private String allergies;

    private String medicalConditions;

    private String insuranceProvider;

    private String insuranceNumber;

    private UUID primaryDoctorId;
}