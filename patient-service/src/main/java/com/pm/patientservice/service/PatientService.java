package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.enums.PatientStatus;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.kafka.PatientProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import events.PatientEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientProducer patientProducer;

    public PatientService(
            PatientRepository patientRepository, PatientProducer patientProducer){

        this.patientRepository = patientRepository;
        this.patientProducer = patientProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    public PatientResponseDTO getPatient(UUID patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient not found with id: " + patientId));

        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO createPatient(
            PatientRequestDTO patientRequestDTO) {

        if (patientRepository.existsByEmail(
                patientRequestDTO.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Patient already exists with email: "
                            + patientRequestDTO.getEmail());
        }

        Patient patient =
                PatientMapper.toEntity(patientRequestDTO);

        Patient savedPatient =
                patientRepository.save(patient);

        PatientEvent event =
                PatientEvent.newBuilder()
                        .setPatientId(savedPatient.getPatientId().toString())
                        .setFirstName(savedPatient.getFirstName())
                        .setLastName(savedPatient.getLastName())
                        .setEmail(savedPatient.getEmail())
                        .setPhone(savedPatient.getPhone())
                        .setGender(savedPatient.getGender().name())
                        .setBloodGroup(savedPatient.getBloodGroup().name())
                        .setEventType("PATIENT_CREATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        patientProducer.publishPatientCreated(event);

        return PatientMapper.toDTO(savedPatient);
    }

    public PatientResponseDTO updatePatient(
            UUID patientId,
            PatientRequestDTO dto) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient not found with id: "
                                        + patientId));

        if (patientRepository.existsByEmailAndPatientIdNot(
                dto.getEmail(),
                patientId)) {

            throw new EmailAlreadyExistsException(
                    "Patient already exists with email: "
                            + dto.getEmail());
        }

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setStatus(dto.getStatus());

        patient.setBloodGroup(dto.getBloodGroup());

        patient.setEmergencyContactName(
                dto.getEmergencyContactName());

        patient.setEmergencyContactPhone(
                dto.getEmergencyContactPhone());

        patient.setAllergies(dto.getAllergies());

        patient.setMedicalConditions(
                dto.getMedicalConditions());

        patient.setInsuranceProvider(
                dto.getInsuranceProvider());

        patient.setInsuranceNumber(
                dto.getInsuranceNumber());

        patient.setPrimaryDoctorId(dto.getPrimaryDoctorId());

        patient.setUpdatedAt(LocalDateTime.now());

        Patient updatedPatient =
                patientRepository.save(patient);

        PatientEvent event =
                PatientEvent.newBuilder()
                        .setPatientId(updatedPatient.getPatientId().toString())
                        .setFirstName(updatedPatient.getFirstName())
                        .setLastName(updatedPatient.getLastName())
                        .setEmail(updatedPatient.getEmail())
                        .setPhone(updatedPatient.getPhone())
                        .setGender(updatedPatient.getGender().name())
                        .setBloodGroup(updatedPatient.getBloodGroup().name())
                        .setEventType("PATIENT_UPDATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        patientProducer.publishPatientUpdated(event);

        return PatientMapper.toDTO(updatedPatient);
    }

    public void deactivatePatient(UUID patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new PatientNotFoundException(
                                "Patient not found with id: "
                                        + patientId));

        patient.setStatus(PatientStatus.INACTIVE);
        patient.setUpdatedAt(LocalDateTime.now());

        patientRepository.save(patient);

    }

    public void deletePatient(UUID id){
        PatientEvent event =
                PatientEvent.newBuilder()
                        .setPatientId(id.toString())
                        .setEventType("PATIENT_DELETED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        patientProducer.publishPatientDeleted(event);
        patientRepository.deleteById(id);
    }

    public List<PatientResponseDTO> searchPatients(
            String name) {

        return patientRepository
                .findByFirstNameContainingIgnoreCase(name)
                .stream()
                .map(PatientMapper::toDTO)
                .toList();
    }
}