package src.main.java.com.pm.prescriptiosvc.mapper;

import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionItemRequestDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionItemResponseDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionRequestDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionResponseDTO;
import src.main.java.com.pm.prescriptiosvc.model.Prescription;
import src.main.java.com.pm.prescriptiosvc.model.PrescriptionItem;

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
                        .map(PrescriptionMapper::toItemEntity)
                        .peek(item -> item.setPrescription(prescription))
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

    public static PrescriptionItem toItemEntity(
            PrescriptionItemRequestDTO itemDto) {

        return PrescriptionItem.builder()
                .medicineName(itemDto.getMedicineName())
                .dosage(itemDto.getDosage())
                .frequency(itemDto.getFrequency())
                .durationDays(itemDto.getDurationDays())
                .instructions(itemDto.getInstructions())
                .build();
    }
}