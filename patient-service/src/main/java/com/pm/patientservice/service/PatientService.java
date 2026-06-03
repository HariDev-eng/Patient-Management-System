package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.enums.PatientStatus;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(
            PatientRepository patientRepository,
            BillingServiceGrpcClient billingServiceGrpcClient,
            KafkaProducer kafkaProducer) {

        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
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

        billingServiceGrpcClient.createBillingAccount(
                savedPatient.getPatientId().toString(),
                savedPatient.getFirstName() + " "
                        + savedPatient.getLastName(),
                savedPatient.getEmail()
        );

        kafkaProducer.sendPatientCreatedEvent(savedPatient);

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

        kafkaProducer.sendPatientUpdatedEvent(
                updatedPatient);

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

        kafkaProducer.sendPatientDeactivatedEvent(
                patient);
    }

    public void deletePatient(UUID id){
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