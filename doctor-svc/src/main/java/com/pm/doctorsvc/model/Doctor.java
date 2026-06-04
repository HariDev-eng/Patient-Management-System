package com.pm.doctorsvc.model;

import com.pm.doctorsvc.enums.AvailabilityStatus;
import com.pm.doctorsvc.enums.Specialization;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID doctorId;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    @Column(unique = true)
    private String licenseNumber;

    private Integer experienceYears;

    private Double consultationFee;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
