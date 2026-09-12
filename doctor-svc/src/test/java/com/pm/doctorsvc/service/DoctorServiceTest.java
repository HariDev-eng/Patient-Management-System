package com.pm.doctorsvc.service;

import com.pm.doctorsvc.dto.DoctorRequestDTO;
import com.pm.doctorsvc.dto.DoctorResponseDTO;
import com.pm.doctorsvc.enums.AvailabilityStatus;
import com.pm.doctorsvc.enums.Specialization;
import com.pm.doctorsvc.exception.DoctorNotFoundException;
import com.pm.doctorsvc.exception.EmailAlreadyExistsException;
import com.pm.doctorsvc.exception.LicenseAlreadyExistsException;
import com.pm.doctorsvc.kafka.AnalyticsDoctorProducer;
import com.pm.doctorsvc.model.Doctor;
import com.pm.doctorsvc.repository.DoctorRepository;
import com.pm.doctorsvc.service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AnalyticsDoctorProducer analyticsDoctorProducer;

    @InjectMocks
    private DoctorService doctorService;

    private UUID doctorId;
    private Doctor doctor;
    private DoctorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        doctorId = UUID.randomUUID();

        requestDTO = new DoctorRequestDTO();

        requestDTO.setFirstName("John");
        requestDTO.setLastName("Smith");
        requestDTO.setEmail("john.smith@example.com");
        requestDTO.setPhone("+1-555-0100");
        requestDTO.setSpecialization(
                Specialization.CARDIOLOGIST
        );
        requestDTO.setLicenseNumber("LIC-12345");
        requestDTO.setExperienceYears(10);
        requestDTO.setConsultationFee(500.0);

        doctor = Doctor.builder()
                .doctorId(doctorId)
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@example.com")
                .phone("+1-555-0100")
                .specialization(Specialization.CARDIOLOGIST)
                .licenseNumber("LIC-12345")
                .experienceYears(10)
                .consultationFee(500.0)
                .availabilityStatus(
                        AvailabilityStatus.AVAILABLE
                )
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // =========================================================
    // GET ALL DOCTORS
    // =========================================================

    @Test
    void shouldReturnAllDoctors() {

        when(doctorRepository.findAll())
                .thenReturn(List.of(doctor));

        List<DoctorResponseDTO> result =
                doctorService.getDoctors();

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                doctorId,
                result.get(0).getDoctorId()
        );

        assertEquals(
                "John",
                result.get(0).getFirstName()
        );

        assertEquals(
                "Smith",
                result.get(0).getLastName()
        );

        verify(doctorRepository)
                .findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoDoctorsExist() {

        when(doctorRepository.findAll())
                .thenReturn(List.of());

        List<DoctorResponseDTO> result =
                doctorService.getDoctors();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository)
                .findAll();
    }

    // =========================================================
    // GET DOCTOR BY ID
    // =========================================================

    @Test
    void shouldReturnDoctorById() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        DoctorResponseDTO result =
                doctorService.getDoctorById(doctorId);

        assertNotNull(result);

        assertEquals(
                doctorId,
                result.getDoctorId()
        );

        assertEquals(
                "John",
                result.getFirstName()
        );

        assertEquals(
                "john.smith@example.com",
                result.getEmail()
        );

        assertEquals(
                Specialization.CARDIOLOGIST,
                result.getSpecialization()
        );

        verify(doctorRepository)
                .findById(doctorId);
    }

    @Test
    void shouldThrowExceptionWhenDoctorDoesNotExist() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.getDoctorById(doctorId)
        );

        verify(doctorRepository)
                .findById(doctorId);
    }

    // =========================================================
    // CREATE DOCTOR
    // =========================================================

    @Test
    void shouldCreateDoctorSuccessfully() {

        when(doctorRepository.existsByEmail(
                requestDTO.getEmail()
        )).thenReturn(false);

        when(doctorRepository.existsByLicenseNumber(
                requestDTO.getLicenseNumber()
        )).thenReturn(false);

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        DoctorResponseDTO result =
                doctorService.createDoctor(requestDTO);

        assertNotNull(result);

        assertEquals(
                doctorId,
                result.getDoctorId()
        );

        assertEquals(
                "John",
                result.getFirstName()
        );

        assertEquals(
                "Smith",
                result.getLastName()
        );

        assertEquals(
                AvailabilityStatus.AVAILABLE,
                result.getAvailabilityStatus()
        );

        verify(doctorRepository)
                .existsByEmail(requestDTO.getEmail());

        verify(doctorRepository)
                .existsByLicenseNumber(
                        requestDTO.getLicenseNumber()
                );

        verify(doctorRepository)
                .save(any(Doctor.class));

        ArgumentCaptor<Doctor> doctorCaptor =
                ArgumentCaptor.forClass(Doctor.class);

        verify(analyticsDoctorProducer)
                .publishDoctorCreated(doctorCaptor.capture());

        Doctor publishedDoctor = doctorCaptor.getValue();

        assertEquals(
                requestDTO.getFirstName(),
                publishedDoctor.getFirstName()
        );

        assertEquals(
                requestDTO.getLastName(),
                publishedDoctor.getLastName()
        );

        assertEquals(
                requestDTO.getEmail(),
                publishedDoctor.getEmail()
        );

        assertEquals(
                requestDTO.getLicenseNumber(),
                publishedDoctor.getLicenseNumber()
        );

        assertEquals(
                requestDTO.getSpecialization(),
                publishedDoctor.getSpecialization()
        );

        assertEquals(
                requestDTO.getExperienceYears(),
                publishedDoctor.getExperienceYears()
        );

        assertEquals(
                requestDTO.getConsultationFee(),
                publishedDoctor.getConsultationFee()
        );
    }

    @Test
    void shouldRejectDoctorWhenEmailAlreadyExists() {

        when(doctorRepository.existsByEmail(
                requestDTO.getEmail()
        )).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> doctorService.createDoctor(requestDTO)
        );

        verify(doctorRepository)
                .existsByEmail(requestDTO.getEmail());

        verify(doctorRepository, never())
                .existsByLicenseNumber(anyString());

        verify(doctorRepository, never())
                .save(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishDoctorCreated(any());
    }

    @Test
    void shouldRejectDoctorWhenLicenseAlreadyExists() {

        when(doctorRepository.existsByEmail(
                requestDTO.getEmail()
        )).thenReturn(false);

        when(doctorRepository.existsByLicenseNumber(
                requestDTO.getLicenseNumber()
        )).thenReturn(true);

        assertThrows(
                LicenseAlreadyExistsException.class,
                () -> doctorService.createDoctor(requestDTO)
        );

        verify(doctorRepository)
                .existsByEmail(requestDTO.getEmail());

        verify(doctorRepository)
                .existsByLicenseNumber(
                        requestDTO.getLicenseNumber()
                );

        verify(doctorRepository, never())
                .save(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishDoctorCreated(any());
    }

    // =========================================================
    // UPDATE DOCTOR
    // =========================================================

    @Test
    void shouldUpdateDoctorSuccessfully() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.existsByEmailAndDoctorIdNot(
                requestDTO.getEmail(),
                doctorId
        )).thenReturn(false);

        when(doctorRepository.existsByLicenseNumberAndDoctorIdNot(
                requestDTO.getLicenseNumber(),
                doctorId
        )).thenReturn(false);

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        DoctorResponseDTO result =
                doctorService.updateDoctor(
                        doctorId,
                        requestDTO
                );

        assertNotNull(result);

        assertEquals(
                "John",
                result.getFirstName()
        );

        assertEquals(
                "Smith",
                result.getLastName()
        );

        assertEquals(
                requestDTO.getEmail(),
                result.getEmail()
        );

        assertEquals(
                requestDTO.getSpecialization(),
                result.getSpecialization()
        );

        verify(doctorRepository)
                .findById(doctorId);

        verify(doctorRepository)
                .existsByEmailAndDoctorIdNot(
                        requestDTO.getEmail(),
                        doctorId
                );

        verify(doctorRepository)
                .existsByLicenseNumberAndDoctorIdNot(
                        requestDTO.getLicenseNumber(),
                        doctorId
                );

        verify(doctorRepository)
                .save(doctor);

        verify(analyticsDoctorProducer)
                .publishDoctorUpdated(doctor);
    }

    @Test
    void shouldRejectUpdateWhenEmailBelongsToAnotherDoctor() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.existsByEmailAndDoctorIdNot(
                requestDTO.getEmail(),
                doctorId
        )).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> doctorService.updateDoctor(
                        doctorId,
                        requestDTO
                )
        );

        verify(doctorRepository, never())
                .save(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishDoctorUpdated(any());
    }

    @Test
    void shouldRejectUpdateWhenLicenseBelongsToAnotherDoctor() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.existsByEmailAndDoctorIdNot(
                requestDTO.getEmail(),
                doctorId
        )).thenReturn(false);

        when(doctorRepository.existsByLicenseNumberAndDoctorIdNot(
                requestDTO.getLicenseNumber(),
                doctorId
        )).thenReturn(true);

        assertThrows(
                LicenseAlreadyExistsException.class,
                () -> doctorService.updateDoctor(
                        doctorId,
                        requestDTO
                )
        );

        verify(doctorRepository, never())
                .save(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishDoctorUpdated(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingDoctor() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.updateDoctor(
                        doctorId,
                        requestDTO
                )
        );

        verify(doctorRepository)
                .findById(doctorId);

        verify(doctorRepository, never())
                .save(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishDoctorUpdated(any());
    }

    // =========================================================
    // DELETE DOCTOR
    // =========================================================

    @Test
    void shouldDeleteDoctorSuccessfully() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        doctorService.deleteDoctor(doctorId);

        verify(doctorRepository)
                .findById(doctorId);

        verify(doctorRepository)
                .delete(doctor);

        verify(analyticsDoctorProducer)
                .publishDoctorDeleted(
                        doctorId.toString()
                );
    }

    @Test
    void shouldNotDeleteWhenDoctorDoesNotExist() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.deleteDoctor(doctorId)
        );

        verify(doctorRepository, never())
                .delete(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishDoctorDeleted(anyString());
    }

    // =========================================================
    // SEARCH
    // =========================================================

    @Test
    void shouldSearchDoctorsBySpecialization() {

        when(doctorRepository.findBySpecialization(
                Specialization.CARDIOLOGIST
        )).thenReturn(List.of(doctor));

        List<DoctorResponseDTO> result =
                doctorService.searchBySpecialization(
                        Specialization.CARDIOLOGIST
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                doctorId,
                result.get(0).getDoctorId()
        );

        assertEquals(
                Specialization.CARDIOLOGIST,
                result.get(0).getSpecialization()
        );

        verify(doctorRepository)
                .findBySpecialization(
                        Specialization.CARDIOLOGIST
                );
    }

    @Test
    void shouldReturnEmptyListWhenNoDoctorMatchesSpecialization() {

        when(doctorRepository.findBySpecialization(
                Specialization.CARDIOLOGIST
        )).thenReturn(List.of());

        List<DoctorResponseDTO> result =
                doctorService.searchBySpecialization(
                        Specialization.CARDIOLOGIST
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorRepository)
                .findBySpecialization(
                        Specialization.CARDIOLOGIST
                );
    }

    // =========================================================
    // UPDATE AVAILABILITY
    // =========================================================

    @Test
    void shouldUpdateDoctorAvailability() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        DoctorResponseDTO result =
                doctorService.updateAvailability(
                        doctorId,
                        AvailabilityStatus.UNAVAILABLE
                );

        assertNotNull(result);

        assertEquals(
                AvailabilityStatus.UNAVAILABLE,
                result.getAvailabilityStatus()
        );

        verify(doctorRepository)
                .findById(doctorId);

        verify(doctorRepository)
                .save(doctor);

        verify(analyticsDoctorProducer)
                .publishAvailabilityChanged(doctor);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAvailabilityForNonExistingDoctor() {

        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());

        assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.updateAvailability(
                        doctorId,
                        AvailabilityStatus.UNAVAILABLE
                )
        );

        verify(doctorRepository, never())
                .save(any(Doctor.class));

        verify(analyticsDoctorProducer, never())
                .publishAvailabilityChanged(any());
    }
}