package src.test.java.com.pm.prescriptiosvc;

import events.PrescriptionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionItemRequestDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionRequestDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionResponseDTO;
import src.main.java.com.pm.prescriptiosvc.exception.PrescriptionNotFoundException;
import src.main.java.com.pm.prescriptiosvc.kafka.PrescriptionProducer;
import src.main.java.com.pm.prescriptiosvc.model.Prescription;
import src.main.java.com.pm.prescriptiosvc.model.PrescriptionItem;
import src.main.java.com.pm.prescriptiosvc.repository.PrescriptionRepository;
import src.main.java.com.pm.prescriptiosvc.service.PrescriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrescriptionSvcApplicationTests {

    private PrescriptionRepository repository;
    private PrescriptionProducer prescriptionProducer;
    private PrescriptionService prescriptionService;

    private UUID prescriptionId;
    private UUID patientId;
    private UUID doctorId;
    private UUID diagnosisId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {

        repository = mock(PrescriptionRepository.class);
        prescriptionProducer = mock(PrescriptionProducer.class);

        prescriptionService =
                new PrescriptionService(
                        repository,
                        prescriptionProducer);

        prescriptionId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        diagnosisId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
    }

    private Prescription createPrescription() {

        PrescriptionItem item =
                PrescriptionItem.builder()
                        .itemId(UUID.randomUUID())
                        .medicineName("Paracetamol")
                        .dosage("500mg")
                        .frequency("Twice a day")
                        .durationDays(5)
                        .instructions("After food")
                        .build();

        Prescription prescription =
                Prescription.builder()
                        .prescriptionId(prescriptionId)
                        .patientId(patientId)
                        .doctorId(doctorId)
                        .diagnosisId(diagnosisId)
                        .appointmentId(appointmentId)
                        .createdAt(LocalDateTime.now())
                        .build();

        item.setPrescription(prescription);

        prescription.setItems(List.of(item));

        return prescription;
    }

    private PrescriptionRequestDTO createRequest() {

        PrescriptionItemRequestDTO item =
                new PrescriptionItemRequestDTO();

        item.setMedicineName("Paracetamol");
        item.setDosage("500mg");
        item.setFrequency("Twice a day");
        item.setDurationDays(5);
        item.setInstructions("After food");

        PrescriptionRequestDTO request =
                new PrescriptionRequestDTO();

        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosisId(diagnosisId);
        request.setAppointmentId(appointmentId);
        request.setItems(List.of(item));

        return request;
    }

    // ------------------------------------------------
    // CREATE
    // ------------------------------------------------

    @Test
    void createPrescription_shouldSaveAndPublishEvent() {

        Prescription prescription = createPrescription();

        when(repository.save(any(Prescription.class)))
                .thenReturn(prescription);

        PrescriptionResponseDTO result =
                prescriptionService.createPrescription(
                        createRequest());

        assertNotNull(result);
        assertEquals(
                prescriptionId,
                result.getPrescriptionId());

        assertEquals(
                patientId,
                result.getPatientId());

        assertEquals(
                doctorId,
                result.getDoctorId());

        assertEquals(
                1,
                result.getItems().size());

        verify(repository).save(any(Prescription.class));

        verify(prescriptionProducer)
                .publishPrescriptionCreated(
                        any(PrescriptionEvent.class));
    }

    @Test
    void createPrescription_shouldPublishCorrectEvent() {

        Prescription prescription = createPrescription();

        when(repository.save(any(Prescription.class)))
                .thenReturn(prescription);

        prescriptionService.createPrescription(
                createRequest());

        ArgumentCaptor<PrescriptionEvent> captor =
                ArgumentCaptor.forClass(
                        PrescriptionEvent.class);

        verify(prescriptionProducer)
                .publishPrescriptionCreated(
                        captor.capture());

        PrescriptionEvent event =
                captor.getValue();

        assertEquals(
                prescriptionId.toString(),
                event.getPrescriptionId());

        assertEquals(
                patientId.toString(),
                event.getPatientId());

        assertEquals(
                doctorId.toString(),
                event.getDoctorId());

        assertEquals(
                diagnosisId.toString(),
                event.getDiagnosisId());

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId());

        assertEquals(
                1,
                event.getMedicineCount());

        assertEquals(
                "PRESCRIPTION_CREATED",
                event.getEventType());
    }

    // ------------------------------------------------
    // GET BY ID
    // ------------------------------------------------

    @Test
    void getPrescription_shouldReturnPrescription() {

        Prescription prescription =
                createPrescription();

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.of(prescription));

        PrescriptionResponseDTO result =
                prescriptionService.getPrescription(
                        prescriptionId);

        assertNotNull(result);

        assertEquals(
                prescriptionId,
                result.getPrescriptionId());

        assertEquals(
                patientId,
                result.getPatientId());

        verify(repository)
                .findById(prescriptionId);
    }

    @Test
    void getPrescription_shouldThrowWhenNotFound() {

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.empty());

        assertThrows(
                PrescriptionNotFoundException.class,
                () -> prescriptionService
                        .getPrescription(prescriptionId));

        verify(repository)
                .findById(prescriptionId);
    }

    // ------------------------------------------------
    // GET ALL
    // ------------------------------------------------

    @Test
    void getAllPrescriptions_shouldReturnAllPrescriptions() {

        Prescription prescription =
                createPrescription();

        when(repository.findAll())
                .thenReturn(List.of(prescription));

        List<PrescriptionResponseDTO> result =
                prescriptionService
                        .getAllPrescriptions();

        assertNotNull(result);

        assertEquals(
                1,
                result.size());

        assertEquals(
                prescriptionId,
                result.get(0).getPrescriptionId());

        verify(repository).findAll();
    }

    @Test
    void getAllPrescriptions_shouldReturnEmptyList() {

        when(repository.findAll())
                .thenReturn(List.of());

        List<PrescriptionResponseDTO> result =
                prescriptionService
                        .getAllPrescriptions();

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(repository).findAll();
    }

    // ------------------------------------------------
    // GET BY PATIENT
    // ------------------------------------------------

    @Test
    void getPatientPrescriptions_shouldReturnPatientPrescriptions() {

        Prescription prescription =
                createPrescription();

        when(repository.findByPatientId(patientId))
                .thenReturn(List.of(prescription));

        List<PrescriptionResponseDTO> result =
                prescriptionService
                        .getPatientPrescriptions(patientId);

        assertNotNull(result);

        assertEquals(
                1,
                result.size());

        assertEquals(
                patientId,
                result.get(0).getPatientId());

        verify(repository)
                .findByPatientId(patientId);
    }

    @Test
    void getPatientPrescriptions_shouldReturnEmptyList() {

        when(repository.findByPatientId(patientId))
                .thenReturn(List.of());

        List<PrescriptionResponseDTO> result =
                prescriptionService
                        .getPatientPrescriptions(patientId);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(repository)
                .findByPatientId(patientId);
    }

    // ------------------------------------------------
    // UPDATE
    // ------------------------------------------------

    @Test
    void updatePrescription_shouldUpdateAndPublishEvent() {

        Prescription existing =
                createPrescription();

        PrescriptionRequestDTO request =
                createRequest();

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(Prescription.class)))
                .thenReturn(existing);

        PrescriptionResponseDTO result =
                prescriptionService.updatePrescription(
                        prescriptionId,
                        request);

        assertNotNull(result);

        assertEquals(
                prescriptionId,
                result.getPrescriptionId());

        assertEquals(
                patientId,
                result.getPatientId());

        assertEquals(
                1,
                result.getItems().size());

        verify(repository)
                .findById(prescriptionId);

        verify(repository)
                .save(existing);

        verify(prescriptionProducer)
                .publishPrescriptionUpdated(
                        any(PrescriptionEvent.class));
    }

    @Test
    void updatePrescription_shouldPublishCorrectEvent() {

        Prescription existing =
                createPrescription();

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(Prescription.class)))
                .thenReturn(existing);

        prescriptionService.updatePrescription(
                prescriptionId,
                createRequest());

        ArgumentCaptor<PrescriptionEvent> captor =
                ArgumentCaptor.forClass(
                        PrescriptionEvent.class);

        verify(prescriptionProducer)
                .publishPrescriptionUpdated(
                        captor.capture());

        PrescriptionEvent event =
                captor.getValue();

        assertEquals(
                prescriptionId.toString(),
                event.getPrescriptionId());

        assertEquals(
                "PRESCRIPTION_UPDATED",
                event.getEventType());

        assertEquals(
                1,
                event.getMedicineCount());
    }

    @Test
    void updatePrescription_shouldThrowWhenNotFound() {

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.empty());

        assertThrows(
                PrescriptionNotFoundException.class,
                () -> prescriptionService
                        .updatePrescription(
                                prescriptionId,
                                createRequest()));

        verify(repository)
                .findById(prescriptionId);

        verify(repository, never())
                .save(any());

        verifyNoInteractions(
                prescriptionProducer);
    }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    @Test
    void deletePrescription_shouldPublishEventAndDelete() {

        Prescription prescription =
                createPrescription();

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.of(prescription));

        prescriptionService.deletePrescription(
                prescriptionId);

        verify(prescriptionProducer)
                .publishPrescriptionDeleted(
                        any(PrescriptionEvent.class));

        verify(repository)
                .delete(prescription);
    }

    @Test
    void deletePrescription_shouldPublishCorrectEvent() {

        Prescription prescription =
                createPrescription();

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.of(prescription));

        prescriptionService.deletePrescription(
                prescriptionId);

        ArgumentCaptor<PrescriptionEvent> captor =
                ArgumentCaptor.forClass(
                        PrescriptionEvent.class);

        verify(prescriptionProducer)
                .publishPrescriptionDeleted(
                        captor.capture());

        PrescriptionEvent event =
                captor.getValue();

        assertEquals(
                prescriptionId.toString(),
                event.getPrescriptionId());

        assertEquals(
                patientId.toString(),
                event.getPatientId());

        assertEquals(
                doctorId.toString(),
                event.getDoctorId());

        assertEquals(
                "PRESCRIPTION_DELETED",
                event.getEventType());

        assertEquals(
                1,
                event.getMedicineCount());
    }

    @Test
    void deletePrescription_shouldThrowWhenNotFound() {

        when(repository.findById(prescriptionId))
                .thenReturn(Optional.empty());

        assertThrows(
                PrescriptionNotFoundException.class,
                () -> prescriptionService
                        .deletePrescription(
                                prescriptionId));

        verify(repository)
                .findById(prescriptionId);

        verify(repository, never())
                .delete(any());

        verifyNoInteractions(
                prescriptionProducer);
    }
}