package com.pm.prescriptiosvc.service;

import com.pm.prescriptiosvc.dto.PrescriptionRequestDTO;
import com.pm.prescriptiosvc.dto.PrescriptionResponseDTO;
import com.pm.prescriptiosvc.exception.PrescriptionNotFoundException;
import com.pm.prescriptiosvc.mapper.PrescriptionMapper;
import com.pm.prescriptiosvc.model.Prescription;
import com.pm.prescriptiosvc.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repository;

    public PrescriptionResponseDTO createPrescription(
            PrescriptionRequestDTO dto) {

        Prescription saved =
                repository.save(
                        PrescriptionMapper.toEntity(dto));

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

        repository.delete(existing);

        Prescription updated =
                PrescriptionMapper.toEntity(dto);

        updated.setPrescriptionId(id);

        Prescription saved =
                repository.save(updated);

        return PrescriptionMapper.toDTO(saved);
    }

    public void deletePrescription(
            UUID id) {

        Prescription prescription =
                repository.findById(id)
                        .orElseThrow(() ->
                                new PrescriptionNotFoundException(
                                        "Prescription not found"));

        repository.delete(prescription);
    }
}