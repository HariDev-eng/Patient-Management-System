package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.enums.PatientStatus;
import com.pm.patientservice.model.Patient;

import java.time.LocalDateTime;

public class PatientMapper {

    private PatientMapper() {
    }

    public static Patient toEntity(PatientRequestDTO dto) {

        return Patient.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .bloodGroup(dto.getBloodGroup())
                .emergencyContactName(dto.getEmergencyContactName())
                .emergencyContactPhone(dto.getEmergencyContactPhone())
                .allergies(dto.getAllergies())
                .medicalConditions(dto.getMedicalConditions())
                .insuranceProvider(dto.getInsuranceProvider())
                .insuranceNumber(dto.getInsuranceNumber())
                .primaryDoctorId(dto.getPrimaryDoctorId())
                .status(PatientStatus.ACTIVE)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static PatientResponseDTO toDTO(Patient patient) {

        return PatientResponseDTO.builder()
                .patientId(patient.getPatientId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .bloodGroup(patient.getBloodGroup())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .allergies(patient.getAllergies())
                .medicalConditions(patient.getMedicalConditions())
                .insuranceProvider(patient.getInsuranceProvider())
                .insuranceNumber(patient.getInsuranceNumber())
                .primaryDoctorId(patient.getPrimaryDoctorId())
                .status(patient.getStatus())
                .registeredAt(patient.getRegisteredAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}