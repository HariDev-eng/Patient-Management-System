package com.pm.patientservice.model;

import com.pm.patientservice.enums.BloodGroup;
import com.pm.patientservice.enums.Gender;
import com.pm.patientservice.enums.PatientStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID patientId;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;

    @Email
    @Column(unique = true)
    private String email;

    private String address;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private String allergies;

    private String medicalConditions;

    private String insuranceProvider;

    private String insuranceNumber;

    private UUID primaryDoctorId;

    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    private LocalDateTime registeredAt;

    private LocalDateTime updatedAt;
}