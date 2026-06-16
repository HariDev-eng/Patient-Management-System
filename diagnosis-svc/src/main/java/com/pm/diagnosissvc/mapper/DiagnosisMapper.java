package com.pm.diagnosissvc.mapper;


import com.pm.diagnosissvc.dto.DiagnosisRequestDTO;
import com.pm.diagnosissvc.dto.DiagnosisResponseDTO;
import com.pm.diagnosissvc.model.Diagnosis;

public class DiagnosisMapper {

    public static Diagnosis toEntity(
            DiagnosisRequestDTO dto) {

        return Diagnosis.builder()
                .patientId(dto.getPatientId())
                .doctorId(dto.getDoctorId())
                .appointmentId(dto.getAppointmentId())
                .symptoms(dto.getSymptoms())
                .diagnosis(dto.getDiagnosis())
                .treatmentPlan(dto.getTreatmentPlan())
                .notes(dto.getNotes())
                .followUpDate(dto.getFollowUpDate())
                .build();
    }

    public static DiagnosisResponseDTO toDTO(
            Diagnosis diagnosis) {

        return DiagnosisResponseDTO.builder()
                .diagnosisId(
                        diagnosis.getDiagnosisId())
                .patientId(
                        diagnosis.getPatientId())
                .doctorId(
                        diagnosis.getDoctorId())
                .appointmentId(
                        diagnosis.getAppointmentId())
                .symptoms(
                        diagnosis.getSymptoms())
                .diagnosis(
                        diagnosis.getDiagnosis())
                .treatmentPlan(
                        diagnosis.getTreatmentPlan())
                .notes(
                        diagnosis.getNotes())
                .followUpDate(
                        diagnosis.getFollowUpDate())
                .createdAt(
                        diagnosis.getCreatedAt())
                .build();
    }
}