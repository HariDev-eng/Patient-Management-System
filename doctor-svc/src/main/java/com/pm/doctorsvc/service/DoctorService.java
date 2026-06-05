package com.pm.doctorsvc.service;

import com.pm.doctorsvc.dto.DoctorRequestDTO;
import com.pm.doctorsvc.dto.DoctorResponseDTO;
import com.pm.doctorsvc.enums.Specialization;
import com.pm.doctorsvc.exception.DoctorNotFoundException;
import com.pm.doctorsvc.exception.EmailAlreadyExistsException;
import com.pm.doctorsvc.exception.LicenseAlreadyExistsException;
import com.pm.doctorsvc.mapper.DoctorMapper;
import com.pm.doctorsvc.model.Doctor;
import com.pm.doctorsvc.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorResponseDTO> getDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::toDTO)
                .toList();
    }

    public DoctorResponseDTO getDoctorById(UUID doctorId) {
        return DoctorMapper.toDTO(getDoctorEntity(doctorId));
    }

    public DoctorResponseDTO createDoctor(
            DoctorRequestDTO doctorRequestDTO) {

        if (doctorRepository.existsByEmail(
                doctorRequestDTO.getEmail())) {

            throw new EmailAlreadyExistsException(
                    "Doctor already exists with email: "
                            + doctorRequestDTO.getEmail());
        }

        if (doctorRepository.existsByLicenseNumber(
                doctorRequestDTO.getLicenseNumber())) {

            throw new LicenseAlreadyExistsException(
                    "Doctor already exists with license number: "
                            + doctorRequestDTO.getLicenseNumber());
        }

        Doctor doctor =
                DoctorMapper.toEntity(doctorRequestDTO);

        Doctor savedDoctor =
                doctorRepository.save(doctor);

        return DoctorMapper.toDTO(savedDoctor);
    }

    public DoctorResponseDTO updateDoctor(
            UUID doctorId,
            DoctorRequestDTO doctorRequestDTO) {

        Doctor existingDoctor =
                getDoctorEntity(doctorId);

        if (doctorRepository.existsByEmailAndDoctorIdNot(
                doctorRequestDTO.getEmail(),
                doctorId)) {

            throw new EmailAlreadyExistsException(
                    "Doctor already exists with email: "
                            + doctorRequestDTO.getEmail());
        }

        if (doctorRepository.existsByLicenseNumberAndDoctorIdNot(
                doctorRequestDTO.getLicenseNumber(),
                doctorId)) {

            throw new LicenseAlreadyExistsException(
                    "Doctor already exists with license number: "
                            + doctorRequestDTO.getLicenseNumber());
        }

        existingDoctor.setFirstName(
                doctorRequestDTO.getFirstName());

        existingDoctor.setLastName(
                doctorRequestDTO.getLastName());

        existingDoctor.setEmail(
                doctorRequestDTO.getEmail());

        existingDoctor.setPhone(
                doctorRequestDTO.getPhone());

        existingDoctor.setSpecialization(
                doctorRequestDTO.getSpecialization());

        existingDoctor.setLicenseNumber(
                doctorRequestDTO.getLicenseNumber());

        existingDoctor.setExperienceYears(
                doctorRequestDTO.getExperienceYears());

        existingDoctor.setConsultationFee(
                doctorRequestDTO.getConsultationFee());

        existingDoctor.setUpdatedAt(
                LocalDateTime.now());

        Doctor updatedDoctor =
                doctorRepository.save(existingDoctor);

        return DoctorMapper.toDTO(updatedDoctor);
    }

    public void deleteDoctor(UUID doctorId) {

        Doctor doctor =
                getDoctorEntity(doctorId);

        doctorRepository.delete(doctor);
    }

    public List<DoctorResponseDTO> searchBySpecialization(
            Specialization specialization) {

        return doctorRepository
                .findBySpecialization(specialization)
                .stream()
                .map(DoctorMapper::toDTO)
                .toList();
    }

    private Doctor getDoctorEntity(UUID doctorId) {

        return doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new DoctorNotFoundException(
                                "Doctor not found with id: "
                                        + doctorId));
    }
}