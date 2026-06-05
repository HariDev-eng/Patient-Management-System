package com.pm.doctorsvc.mapper;

import com.pm.doctorsvc.dto.DoctorRequestDTO;
import com.pm.doctorsvc.dto.DoctorResponseDTO;
import com.pm.doctorsvc.enums.AvailabilityStatus;
import com.pm.doctorsvc.model.Doctor;

import java.time.LocalDateTime;

public class DoctorMapper {

    private DoctorMapper() {
    }

    public static Doctor toEntity(DoctorRequestDTO dto) {

        return Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .specialization(dto.getSpecialization())
                .licenseNumber(dto.getLicenseNumber())
                .experienceYears(dto.getExperienceYears())
                .consultationFee(dto.getConsultationFee())
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static DoctorResponseDTO toDTO(Doctor doctor) {

        return DoctorResponseDTO.builder()
                .doctorId(doctor.getDoctorId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .licenseNumber(doctor.getLicenseNumber())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .availabilityStatus(doctor.getAvailabilityStatus())
                .build();
    }
}