package com.pm.nurseservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vital_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID vitalId;

    private UUID patientId;

    private UUID nurseId;

    private Double temperature;

    private Integer heartRate;

    private Integer systolicBP;

    private Integer diastolicBP;

    private Double weight;

    private Double height;

    private Integer oxygenSaturation;

    @CreationTimestamp
    private LocalDateTime recordedAt;
}