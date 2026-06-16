package com.pm.diagnosissvc.service;

import com.pm.diagnosissvc.dto.DiagnosisRequestDTO;
import com.pm.diagnosissvc.dto.DiagnosisResponseDTO;
import com.pm.diagnosissvc.exception.DiagnosisNotFoundException;
import com.pm.diagnosissvc.mapper.DiagnosisMapper;
import com.pm.diagnosissvc.model.Diagnosis;
import com.pm.diagnosissvc.repository.DiagnosisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisResponseDTO createDiagnosis(
            DiagnosisRequestDTO dto) {

        Diagnosis diagnosis =
                DiagnosisMapper.toEntity(dto);

        Diagnosis saved =
                diagnosisRepository.save(diagnosis);

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

        diagnosisRepository.delete(diagnosis);
    }
}