package com.pm.appointmentservice;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.enums.AppointmentStatus;
import com.pm.appointmentservice.grpc.PatientGrpcClient;
import com.pm.appointmentservice.kafka.AppointmentProducer;
import com.pm.appointmentservice.model.Appointment;
import com.pm.appointmentservice.repository.AppointmentRepository;
import com.pm.appointmentservice.service.AppointmentService;
import events.AppointmentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceApplicationTests {

    private AppointmentRepository appointmentRepository;
    private PatientGrpcClient patientGrpcClient;
    private AppointmentProducer appointmentProducer;

    private AppointmentService appointmentService;

    private UUID appointmentId;
    private UUID patientId;
    private UUID doctorId;

    @BeforeEach
    void setUp() {

        appointmentRepository = mock(AppointmentRepository.class);
        patientGrpcClient = mock(PatientGrpcClient.class);
        appointmentProducer = mock(AppointmentProducer.class);

        appointmentService =
                new AppointmentService(
                        appointmentRepository,
                        patientGrpcClient,
                        appointmentProducer
                );

        appointmentId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
    }

    // ------------------------------------------------
    // TEST DATA
    // ------------------------------------------------

    private AppointmentRequestDTO createRequest() {

        return AppointmentRequestDTO.builder()
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentDateTime(LocalDate.now().plusDays(5))
                .reason("Regular checkup")
                .notes("First visit")
                .build();
    }

    private Appointment createAppointment() {

        return Appointment.builder()
                .appointmentId(appointmentId)
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentDateTime(LocalDate.now().plusDays(5))
                .status(AppointmentStatus.SCHEDULED)
                .reason("Regular checkup")
                .notes("First visit")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    // ------------------------------------------------
    // CREATE
    // ------------------------------------------------

    @Test
    void createAppointment_shouldSaveAndPublishEvent() {

        Appointment appointment = createAppointment();

        when(patientGrpcClient.patientExists(patientId))
                .thenReturn(true);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        AppointmentResponseDTO result =
                appointmentService.createAppointment(createRequest());

        assertNotNull(result);

        assertEquals(
                appointmentId,
                result.getAppointmentId()
        );

        assertEquals(
                patientId,
                result.getPatientId()
        );

        assertEquals(
                doctorId,
                result.getDoctorId()
        );

        assertEquals(
                AppointmentStatus.SCHEDULED,
                result.getStatus()
        );

        verify(patientGrpcClient)
                .patientExists(patientId);

        verify(appointmentRepository)
                .save(any(Appointment.class));

        verify(appointmentProducer)
                .publishAppointmentCreated(
                        any(AppointmentEvent.class)
                );
    }


    @Test
    void createAppointment_shouldSetStatusToScheduled() {

        Appointment appointment = createAppointment();

        when(patientGrpcClient.patientExists(patientId))
                .thenReturn(true);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        appointmentService.createAppointment(createRequest());

        ArgumentCaptor<Appointment> captor =
                ArgumentCaptor.forClass(Appointment.class);

        verify(appointmentRepository)
                .save(captor.capture());

        Appointment saved =
                captor.getValue();

        assertEquals(
                AppointmentStatus.SCHEDULED,
                saved.getStatus()
        );
    }


    @Test
    void createAppointment_shouldPublishCorrectEvent() {

        Appointment appointment = createAppointment();

        when(patientGrpcClient.patientExists(patientId))
                .thenReturn(true);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        appointmentService.createAppointment(createRequest());

        ArgumentCaptor<AppointmentEvent> captor =
                ArgumentCaptor.forClass(
                        AppointmentEvent.class
                );

        verify(appointmentProducer)
                .publishAppointmentCreated(
                        captor.capture()
                );

        AppointmentEvent event =
                captor.getValue();

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId()
        );

        assertEquals(
                patientId.toString(),
                event.getPatientId()
        );

        assertEquals(
                doctorId.toString(),
                event.getDoctorId()
        );

        assertEquals(
                "Regular checkup",
                event.getReason()
        );

        assertEquals(
                "SCHEDULED",
                event.getStatus()
        );

        assertEquals(
                "APPOINTMENT_CREATED",
                event.getEventType()
        );

        assertFalse(
                event.getOccurredAt().isEmpty()
        );
    }


    @Test
    void createAppointment_shouldThrowWhenPatientDoesNotExist() {

        when(patientGrpcClient.patientExists(patientId))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> appointmentService
                                .createAppointment(createRequest())
                );

        assertEquals(
                "Patient not found",
                exception.getMessage()
        );

        verify(patientGrpcClient)
                .patientExists(patientId);

        verify(appointmentRepository, never())
                .save(any());

        verifyNoInteractions(
                appointmentProducer
        );
    }


    @Test
    void createAppointment_shouldRejectPastAppointment() {

        AppointmentRequestDTO request =
                createRequest();

        request.setAppointmentDateTime(
                LocalDate.now().minusDays(1)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> appointmentService
                                .createAppointment(request)
                );

        assertEquals(
                "Appointment cannot be scheduled in the past",
                exception.getMessage()
        );

        verifyNoInteractions(
                patientGrpcClient,
                appointmentRepository,
                appointmentProducer
        );
    }


    // ------------------------------------------------
    // GET BY ID
    // ------------------------------------------------

    @Test
    void getAppointmentById_shouldReturnAppointment() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        AppointmentResponseDTO result =
                appointmentService.getAppointmentById(
                        appointmentId
                );

        assertNotNull(result);

        assertEquals(
                appointmentId,
                result.getAppointmentId()
        );

        assertEquals(
                patientId,
                result.getPatientId()
        );

        verify(appointmentRepository)
                .findById(appointmentId);
    }


    @Test
    void getAppointmentById_shouldThrowWhenNotFound() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> appointmentService
                                .getAppointmentById(appointmentId)
                );

        assertEquals(
                "Appointment not found with id: "
                        + appointmentId,
                exception.getMessage()
        );

        verify(appointmentRepository)
                .findById(appointmentId);
    }


    // ------------------------------------------------
    // UPDATE STATUS
    // ------------------------------------------------

    @Test
    void updateAppointmentStatus_shouldUpdateAndPublishEvent() {

        Appointment appointment =
                createAppointment();

        AppointmentRequestDTO request =
                createRequest();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        AppointmentResponseDTO result =
                appointmentService.updateAppointmentStatus(
                        appointmentId,
                        AppointmentStatus.CONFIRMED,
                        request
                );

        assertNotNull(result);

        assertEquals(
                AppointmentStatus.CONFIRMED,
                result.getStatus()
        );

        assertEquals(
                "Regular checkup",
                result.getReason()
        );

        assertEquals(
                "First visit",
                result.getNotes()
        );

        verify(appointmentRepository)
                .findById(appointmentId);

        verify(appointmentRepository)
                .save(appointment);

        verify(appointmentProducer)
                .publishAppointmentUpdated(
                        any(AppointmentEvent.class)
                );
    }


    @Test
    void updateAppointmentStatus_shouldPublishCorrectEvent() {

        Appointment appointment =
                createAppointment();

        AppointmentRequestDTO request =
                createRequest();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        appointmentService.updateAppointmentStatus(
                appointmentId,
                AppointmentStatus.CONFIRMED,
                request
        );

        ArgumentCaptor<AppointmentEvent> captor =
                ArgumentCaptor.forClass(
                        AppointmentEvent.class
                );

        verify(appointmentProducer)
                .publishAppointmentUpdated(
                        captor.capture()
                );

        AppointmentEvent event =
                captor.getValue();

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId()
        );

        assertEquals(
                patientId.toString(),
                event.getPatientId()
        );

        assertEquals(
                doctorId.toString(),
                event.getDoctorId()
        );

        assertEquals(
                "CONFIRMED",
                event.getStatus()
        );

        assertEquals(
                "APPOINTMENT_UPDATED",
                event.getEventType()
        );
    }


    @Test
    void updateAppointmentStatus_shouldThrowWhenNotFound() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> appointmentService
                        .updateAppointmentStatus(
                                appointmentId,
                                AppointmentStatus.CONFIRMED,
                                createRequest()
                        )
        );

        verify(appointmentRepository)
                .findById(appointmentId);

        verify(appointmentRepository, never())
                .save(any());

        verifyNoInteractions(
                appointmentProducer
        );
    }


    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    @Test
    void deleteAppointment_shouldDeleteAndPublishEvent() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(
                appointmentId
        );

        verify(appointmentProducer)
                .publishAppointmentDeleted(
                        any(AppointmentEvent.class)
                );

        verify(appointmentRepository)
                .delete(appointment);
    }


    @Test
    void deleteAppointment_shouldPublishCorrectEvent() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(
                appointmentId
        );

        ArgumentCaptor<AppointmentEvent> captor =
                ArgumentCaptor.forClass(
                        AppointmentEvent.class
                );

        verify(appointmentProducer)
                .publishAppointmentDeleted(
                        captor.capture()
                );

        AppointmentEvent event =
                captor.getValue();

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId()
        );

        assertEquals(
                "APPOINTMENT_DELETED",
                event.getEventType()
        );

        assertFalse(
                event.getOccurredAt().isEmpty()
        );
    }


    @Test
    void deleteAppointment_shouldThrowWhenNotFound() {

        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> appointmentService
                        .deleteAppointment(appointmentId)
        );

        verify(appointmentRepository)
                .findById(appointmentId);

        verify(appointmentRepository, never())
                .delete(any());

        verifyNoInteractions(
                appointmentProducer
        );
    }


    // ------------------------------------------------
    // GET BY PATIENT
    // ------------------------------------------------

    @Test
    void getAppointmentsByPatientId_shouldReturnAppointments() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository
                .findByPatientId(patientId))
                .thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAppointmentsByPatientId(patientId);

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                appointmentId,
                result.get(0).getAppointmentId()
        );

        verify(appointmentRepository)
                .findByPatientId(patientId);
    }


    @Test
    void getAppointmentsByPatientId_shouldReturnEmptyList() {

        when(appointmentRepository
                .findByPatientId(patientId))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAppointmentsByPatientId(patientId);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(appointmentRepository)
                .findByPatientId(patientId);
    }


    // ------------------------------------------------
    // GET BY DOCTOR
    // ------------------------------------------------

    @Test
    void getAppointmentsByDoctorId_shouldReturnAppointments() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository
                .findByDoctorId(doctorId))
                .thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAppointmentsByDoctorId(doctorId);

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                doctorId,
                result.get(0).getDoctorId()
        );

        verify(appointmentRepository)
                .findByDoctorId(doctorId);
    }


    @Test
    void getAppointmentsByDoctorId_shouldReturnEmptyList() {

        when(appointmentRepository
                .findByDoctorId(doctorId))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAppointmentsByDoctorId(doctorId);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(appointmentRepository)
                .findByDoctorId(doctorId);
    }


    // ------------------------------------------------
    // GET ALL
    // ------------------------------------------------

    @Test
    void getAllAppointments_shouldReturnAllAppointments() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository.findAll())
                .thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAllAppointments();

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                appointmentId,
                result.get(0).getAppointmentId()
        );

        verify(appointmentRepository)
                .findAll();
    }


    @Test
    void getAllAppointments_shouldReturnEmptyList() {

        when(appointmentRepository.findAll())
                .thenReturn(List.of());

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAllAppointments();

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(appointmentRepository)
                .findAll();
    }


    // ------------------------------------------------
    // GET BY STATUS
    // ------------------------------------------------

    @Test
    void getAppointmentsByStatus_shouldReturnAppointments() {

        Appointment appointment =
                createAppointment();

        when(appointmentRepository
                .findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAppointmentsByStatus(
                                AppointmentStatus.SCHEDULED
                        );

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                AppointmentStatus.SCHEDULED,
                result.get(0).getStatus()
        );

        verify(appointmentRepository)
                .findByStatus(
                        AppointmentStatus.SCHEDULED
                );
    }


    @Test
    void getAppointmentsByStatus_shouldReturnEmptyList() {

        when(appointmentRepository
                .findByStatus(AppointmentStatus.CANCELLED))
                .thenReturn(List.of());

        List<AppointmentResponseDTO> result =
                appointmentService
                        .getAppointmentsByStatus(
                                AppointmentStatus.CANCELLED
                        );

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(appointmentRepository)
                .findByStatus(
                        AppointmentStatus.CANCELLED
                );
    }
}