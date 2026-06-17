package com.pm.prescriptiosvc.mapper;



import com.pm.prescriptiosvc.dto.PrescriptionItemResponseDTO;
import com.pm.prescriptiosvc.dto.PrescriptionRequestDTO;
import com.pm.prescriptiosvc.dto.PrescriptionResponseDTO;
import com.pm.prescriptiosvc.model.Prescription;
import com.pm.prescriptiosvc.model.PrescriptionItem;

import java.util.List;

public class PrescriptionMapper {

    public static Prescription toEntity(
            PrescriptionRequestDTO dto) {

        Prescription prescription =
                Prescription.builder()
                        .patientId(dto.getPatientId())
                        .doctorId(dto.getDoctorId())
                        .diagnosisId(dto.getDiagnosisId())
                        .appointmentId(dto.getAppointmentId())
                        .build();

        List<PrescriptionItem> items =
                dto.getItems()
                        .stream()
                        .map(item ->
                                PrescriptionItem.builder()
                                        .medicineName(item.getMedicineName())
                                        .dosage(item.getDosage())
                                        .frequency(item.getFrequency())
                                        .durationDays(item.getDurationDays())
                                        .instructions(item.getInstructions())
                                        .prescription(prescription)
                                        .build())
                        .toList();

        prescription.setItems(items);

        return prescription;
    }

    public static PrescriptionResponseDTO toDTO(
            Prescription prescription) {

        return PrescriptionResponseDTO.builder()
                .prescriptionId(
                        prescription.getPrescriptionId())
                .patientId(
                        prescription.getPatientId())
                .doctorId(
                        prescription.getDoctorId())
                .diagnosisId(
                        prescription.getDiagnosisId())
                .appointmentId(
                        prescription.getAppointmentId())
                .createdAt(
                        prescription.getCreatedAt())
                .items(
                        prescription.getItems()
                                .stream()
                                .map(item ->
                                        PrescriptionItemResponseDTO
                                                .builder()
                                                .itemId(item.getItemId())
                                                .medicineName(item.getMedicineName())
                                                .dosage(item.getDosage())
                                                .frequency(item.getFrequency())
                                                .durationDays(item.getDurationDays())
                                                .instructions(item.getInstructions())
                                                .build())
                                .toList())
                .build();
    }
}