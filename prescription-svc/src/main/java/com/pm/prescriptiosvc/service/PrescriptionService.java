package src.main.java.com.pm.prescriptiosvc.service;

import events.PrescriptionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionRequestDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionResponseDTO;
import src.main.java.com.pm.prescriptiosvc.exception.PrescriptionNotFoundException;
import src.main.java.com.pm.prescriptiosvc.kafka.PrescriptionProducer;
import src.main.java.com.pm.prescriptiosvc.mapper.PrescriptionMapper;
import src.main.java.com.pm.prescriptiosvc.model.Prescription;
import src.main.java.com.pm.prescriptiosvc.model.PrescriptionItem;
import src.main.java.com.pm.prescriptiosvc.repository.PrescriptionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repository;
    private  final PrescriptionProducer prescriptionProducer;

    public PrescriptionResponseDTO createPrescription(
            PrescriptionRequestDTO dto) {

        Prescription saved =
                repository.save(
                        PrescriptionMapper.toEntity(dto));

        PrescriptionEvent event =
                PrescriptionEvent.newBuilder()
                        .setPrescriptionId(saved.getPrescriptionId().toString())
                        .setPatientId(saved.getPatientId().toString())
                        .setDoctorId(saved.getDoctorId().toString())
                        .setDiagnosisId(saved.getDiagnosisId().toString())
                        .setAppointmentId(saved.getAppointmentId().toString())
                        .setMedicineCount(saved.getItems().size())
                        .setEventType("PRESCRIPTION_CREATED")
                        .setOccurredAt(saved.getCreatedAt().toString())
                        .build();

        prescriptionProducer.publishPrescriptionCreated(event);

        return PrescriptionMapper.toDTO(saved);
    }

    public PrescriptionResponseDTO getPrescription(
            UUID id) {

        Prescription prescription =
                repository.findById(id)
                        .orElseThrow(() ->
                                new PrescriptionNotFoundException(
                                        "Prescription not found"));

        return PrescriptionMapper.toDTO(prescription);
    }

    public List<PrescriptionResponseDTO>
    getAllPrescriptions() {

        return repository.findAll()
                .stream()
                .map(PrescriptionMapper::toDTO)
                .toList();
    }

    public List<PrescriptionResponseDTO>
    getPatientPrescriptions(
            UUID patientId) {

        return repository.findByPatientId(patientId)
                .stream()
                .map(PrescriptionMapper::toDTO)
                .toList();
    }

    public PrescriptionResponseDTO updatePrescription(
            UUID id,
            PrescriptionRequestDTO dto) {

        Prescription existing =
                repository.findById(id)
                        .orElseThrow(() ->
                                new PrescriptionNotFoundException(
                                        "Prescription not found"));

        existing.setPatientId(dto.getPatientId());
        existing.setDoctorId(dto.getDoctorId());
        existing.setDiagnosisId(dto.getDiagnosisId());
        existing.setAppointmentId(dto.getAppointmentId());

        existing.getItems().clear();

        dto.getItems().forEach(itemDto -> {
            PrescriptionItem item =
                    PrescriptionMapper.toItemEntity(itemDto);

            item.setPrescription(existing);

            existing.getItems().add(item);
        });

        Prescription saved =
                repository.save(existing);

        PrescriptionEvent event =
                PrescriptionEvent.newBuilder()
                        .setPrescriptionId(saved.getPrescriptionId().toString())
                        .setPatientId(saved.getPatientId().toString())
                        .setDoctorId(saved.getDoctorId().toString())
                        .setDiagnosisId(saved.getDiagnosisId().toString())
                        .setAppointmentId(saved.getAppointmentId().toString())
                        .setMedicineCount(saved.getItems().size())
                        .setEventType("PRESCRIPTION_UPDATED")
                        .setOccurredAt(saved.getCreatedAt().toString())
                        .build();

        prescriptionProducer.publishPrescriptionUpdated(event);

        return PrescriptionMapper.toDTO(saved);
    }

    public void deletePrescription(UUID id) {

        Prescription prescription =
                repository.findById(id)
                        .orElseThrow(() ->
                                new PrescriptionNotFoundException(
                                        "Prescription not found"));

        PrescriptionEvent event =
                PrescriptionEvent.newBuilder()
                        .setPrescriptionId(
                                prescription.getPrescriptionId().toString())
                        .setPatientId(
                                prescription.getPatientId().toString())
                        .setDoctorId(
                                prescription.getDoctorId().toString())
                        .setDiagnosisId(
                                prescription.getDiagnosisId().toString())
                        .setAppointmentId(
                                prescription.getAppointmentId().toString())
                        .setMedicineCount(
                                prescription.getItems().size())
                        .setEventType("PRESCRIPTION_DELETED")
                        .setOccurredAt(
                                prescription.getCreatedAt().toString())
                        .build();

        prescriptionProducer.publishPrescriptionDeleted(event);

        repository.delete(prescription);
    }
}