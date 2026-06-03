package com.pm.patientservice.dto;

import com.pm.patientservice.enums.BloodGroup;
import com.pm.patientservice.enums.Gender;
import com.pm.patientservice.enums.PatientStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PatientResponseDTO {

    private UUID patientId;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String phone;

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

    private PatientStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime updatedAt;
}