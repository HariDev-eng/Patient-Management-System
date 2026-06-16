package com.pm.diagnosissvc.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID diagnosisId;

    private UUID patientId;

    private UUID doctorId;

    private UUID appointmentId;

    @Column(length = 2000)
    private String symptoms;

    @Column(length = 2000)
    private String diagnosis;

    @Column(length = 3000)
    private String treatmentPlan;

    @Column(length = 3000)
    private String notes;

    private LocalDate followUpDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
}