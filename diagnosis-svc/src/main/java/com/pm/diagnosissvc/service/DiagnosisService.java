package com.pm.diagnosissvc.service;

import com.pm.diagnosissvc.dto.DiagnosisRequestDTO;
import com.pm.diagnosissvc.dto.DiagnosisResponseDTO;
import com.pm.diagnosissvc.exception.DiagnosisNotFoundException;
import com.pm.diagnosissvc.kafka.DiagnosisProducer;
import com.pm.diagnosissvc.mapper.DiagnosisMapper;
import com.pm.diagnosissvc.model.Diagnosis;
import com.pm.diagnosissvc.repository.DiagnosisRepository;
import events.DiagnosisEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisProducer diagnosisProducer;

    public DiagnosisResponseDTO createDiagnosis(
            DiagnosisRequestDTO dto) {

        Diagnosis diagnosis =
                DiagnosisMapper.toEntity(dto);

        Diagnosis saved =
                diagnosisRepository.save(diagnosis);

        DiagnosisEvent event =
                DiagnosisEvent.newBuilder()
                        .setDiagnosisId(saved.getDiagnosisId().toString())
                        .setPatientId(saved.getPatientId().toString())
                        .setDoctorId(saved.getDoctorId().toString())
                        .setAppointmentId(saved.getAppointmentId().toString())
                        .setDiagnosis(saved.getDiagnosis())
                        .setFollowUpRequired(saved.getFollowUpRequired())
                        .setFollowUpDays(saved.getFollowUpDays())
                        .setFollowUpDate(
                                saved.getFollowUpDate() == null
                                        ? ""
                                        : saved.getFollowUpDate().toString())
                        .setEventType("DIAGNOSIS_CREATED")
                        .setOccurredAt(saved.getCreatedAt().toString())
                        .build();

        diagnosisProducer.publishDiagnosisCreated(event);

        return DiagnosisMapper.toDTO(saved);
    }

    public DiagnosisResponseDTO getDiagnosis(
            UUID diagnosisId) {

        Diagnosis diagnosis =
                diagnosisRepository.findById(diagnosisId)
                        .orElseThrow(() ->
                                new DiagnosisNotFoundException(
                                        diagnosisId
                                ));

        return DiagnosisMapper.toDTO(diagnosis);
    }

    public List<DiagnosisResponseDTO> getDiagnoses() {

        return diagnosisRepository.findAll()
                .stream()
                .map(DiagnosisMapper::toDTO)
                .toList();
    }

    public List<DiagnosisResponseDTO> getDiagnosesByPatient(
            UUID patientId) {

        return diagnosisRepository
                .findByPatientId(patientId)
                .stream()
                .map(DiagnosisMapper::toDTO)
                .toList();
    }

    public List<DiagnosisResponseDTO> getDiagnosesByDoctor(
            UUID doctorId) {

        return diagnosisRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(DiagnosisMapper::toDTO)
                .toList();
    }

    public DiagnosisResponseDTO updateDiagnosis(
            UUID diagnosisId,
            DiagnosisRequestDTO dto) {

        Diagnosis diagnosis =
                diagnosisRepository.findById(diagnosisId)
                        .orElseThrow(() ->
                                new DiagnosisNotFoundException(
                                        diagnosisId
                                ));

        diagnosis.setSymptoms(
                dto.getSymptoms());

        diagnosis.setDiagnosis(
                dto.getDiagnosis());

        diagnosis.setTreatmentPlan(
                dto.getTreatmentPlan());

        diagnosis.setNotes(
                dto.getNotes());

        diagnosis.setFollowUpDate(
                dto.getFollowUpDate());

        Diagnosis updated =
                diagnosisRepository.save(diagnosis);

        DiagnosisEvent event =
                DiagnosisEvent.newBuilder()
                        .setDiagnosisId(updated.getDiagnosisId().toString())
                        .setPatientId(updated.getPatientId().toString())
                        .setDoctorId(updated.getDoctorId().toString())
                        .setAppointmentId(updated.getAppointmentId().toString())
                        .setDiagnosis(updated.getDiagnosis())
                        .setFollowUpRequired(updated.getFollowUpRequired())
                        .setFollowUpDays(updated.getFollowUpDays())
                        .setFollowUpDate(
                                updated.getFollowUpDate() == null
                                        ? ""
                                        : updated.getFollowUpDate().toString())
                        .setEventType("DIAGNOSIS_UPDATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        diagnosisProducer.publishDiagnosisUpdated(event);

        return DiagnosisMapper.toDTO(updated);
    }

    public void deleteDiagnosis(
            UUID diagnosisId) {

        Diagnosis diagnosis =
                diagnosisRepository.findById(diagnosisId)
                        .orElseThrow(() ->
                                new DiagnosisNotFoundException(
                                        diagnosisId
                                ));

        DiagnosisEvent event =
                DiagnosisEvent.newBuilder()
                        .setDiagnosisId(diagnosis.getDiagnosisId().toString())
                        .setPatientId(diagnosis.getPatientId().toString())
                        .setDoctorId(diagnosis.getDoctorId().toString())
                        .setAppointmentId(diagnosis.getAppointmentId().toString())
                        .setDiagnosis(diagnosis.getDiagnosis())
                        .setFollowUpRequired(diagnosis.getFollowUpRequired())
                        .setFollowUpDays(diagnosis.getFollowUpDays())
                        .setFollowUpDate(
                                diagnosis.getFollowUpDate() == null
                                        ? ""
                                        : diagnosis.getFollowUpDate().toString())
                        .setEventType("DIAGNOSIS_DELETED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        diagnosisProducer.publishDiagnosisDeleted(event);

        diagnosisRepository.delete(diagnosis);
    }
}