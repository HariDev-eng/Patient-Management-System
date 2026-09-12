package com.pm.nurseservice;

import com.pm.nurseservice.dto.VitalRequestDTO;
import com.pm.nurseservice.dto.VitalResponseDTO;
import com.pm.nurseservice.kafka.VitalsProducer;
import com.pm.nurseservice.model.VitalRecord;
import com.pm.nurseservice.repository.VitalRepository;
import com.pm.nurseservice.service.VitalService;
import events.VitalsRecordedEvent;
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
class VitalServiceTest {

    @Mock
    private VitalRepository vitalRepository;

    @Mock
    private VitalsProducer vitalsProducer;

    @InjectMocks
    private VitalService vitalService;

    private UUID vitalId;
    private UUID patientId;
    private UUID nurseId;

    private VitalRecord vitalRecord;
    private VitalRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        vitalId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        nurseId = UUID.randomUUID();

        requestDTO = new VitalRequestDTO();

        requestDTO.setPatientId(patientId);
        requestDTO.setNurseId(nurseId);
        requestDTO.setTemperature(98.6);
        requestDTO.setHeartRate(72);
        requestDTO.setSystolicBP(120);
        requestDTO.setDiastolicBP(80);
        requestDTO.setWeight(70.5);
        requestDTO.setHeight(175.0);
        requestDTO.setOxygenSaturation(98);

        vitalRecord = VitalRecord.builder()
                .vitalId(vitalId)
                .patientId(patientId)
                .nurseId(nurseId)
                .temperature(98.6)
                .heartRate(72)
                .systolicBP(120)
                .diastolicBP(80)
                .weight(70.5)
                .height(175.0)
                .oxygenSaturation(98)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    // =========================================================
    // CREATE VITAL
    // =========================================================

    @Test
    void shouldCreateVitalSuccessfully() {

        when(vitalRepository.save(any(VitalRecord.class)))
                .thenReturn(vitalRecord);

        VitalResponseDTO result =
                vitalService.createVital(requestDTO);

        assertNotNull(result);

        assertEquals(
                vitalId,
                result.getVitalId()
        );

        assertEquals(
                patientId,
                result.getPatientId()
        );

        assertEquals(
                nurseId,
                result.getNurseId()
        );

        assertEquals(
                98.6,
                result.getTemperature()
        );

        assertEquals(
                72,
                result.getHeartRate()
        );

        assertEquals(
                120,
                result.getSystolicBP()
        );

        assertEquals(
                80,
                result.getDiastolicBP()
        );

        assertEquals(
                70.5,
                result.getWeight()
        );

        assertEquals(
                175.0,
                result.getHeight()
        );

        assertEquals(
                98,
                result.getOxygenSaturation()
        );

        verify(vitalRepository)
                .save(any(VitalRecord.class));

        verify(vitalsProducer)
                .publishVitalsRecorded(any(VitalsRecordedEvent.class));
    }

    @Test
    void shouldPublishCorrectVitalsEvent() {

        when(vitalRepository.save(any(VitalRecord.class)))
                .thenReturn(vitalRecord);

        vitalService.createVital(requestDTO);

        ArgumentCaptor<VitalsRecordedEvent> eventCaptor =
                ArgumentCaptor.forClass(
                        VitalsRecordedEvent.class
                );

        verify(vitalsProducer)
                .publishVitalsRecorded(
                        eventCaptor.capture()
                );

        VitalsRecordedEvent event =
                eventCaptor.getValue();

        assertEquals(
                vitalId.toString(),
                event.getVitalId()
        );

        assertEquals(
                patientId.toString(),
                event.getPatientId()
        );

        assertEquals(
                nurseId.toString(),
                event.getNurseId()
        );

        assertEquals(
                98.6,
                event.getTemperature()
        );

        assertEquals(
                72,
                event.getHeartRate()
        );

        assertEquals(
                120,
                event.getSystolicBP()
        );

        assertEquals(
                80,
                event.getDiastolicBP()
        );

        assertEquals(
                70.5,
                event.getWeight()
        );

        assertEquals(
                175.0,
                event.getHeight()
        );

        assertEquals(
                98,
                event.getOxygenSaturation()
        );

        assertEquals(
                vitalRecord.getRecordedAt().toString(),
                event.getRecordedAt()
        );
    }

    // =========================================================
    // GET VITAL BY ID
    // =========================================================

    @Test
    void shouldReturnVitalById() {

        when(vitalRepository.findById(vitalId))
                .thenReturn(Optional.of(vitalRecord));

        VitalResponseDTO result =
                vitalService.getVitalById(vitalId);

        assertNotNull(result);

        assertEquals(
                vitalId,
                result.getVitalId()
        );

        assertEquals(
                patientId,
                result.getPatientId()
        );

        assertEquals(
                nurseId,
                result.getNurseId()
        );

        assertEquals(
                98.6,
                result.getTemperature()
        );

        assertEquals(
                72,
                result.getHeartRate()
        );

        verify(vitalRepository)
                .findById(vitalId);
    }

    @Test
    void shouldThrowExceptionWhenVitalDoesNotExist() {

        when(vitalRepository.findById(vitalId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> vitalService.getVitalById(vitalId)
                );

        assertEquals(
                "Vital not found",
                exception.getMessage()
        );

        verify(vitalRepository)
                .findById(vitalId);
    }

    // =========================================================
    // GET VITALS BY PATIENT
    // =========================================================

    @Test
    void shouldReturnVitalsForPatient() {

        when(vitalRepository.findByPatientId(patientId))
                .thenReturn(List.of(vitalRecord));

        List<VitalResponseDTO> result =
                vitalService.getVitalsByPatient(patientId);

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                vitalId,
                result.get(0).getVitalId()
        );

        assertEquals(
                patientId,
                result.get(0).getPatientId()
        );

        verify(vitalRepository)
                .findByPatientId(patientId);
    }

    @Test
    void shouldReturnEmptyListWhenPatientHasNoVitals() {

        when(vitalRepository.findByPatientId(patientId))
                .thenReturn(List.of());

        List<VitalResponseDTO> result =
                vitalService.getVitalsByPatient(patientId);

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(vitalRepository)
                .findByPatientId(patientId);
    }

    // =========================================================
    // DELETE VITAL
    // =========================================================

    @Test
    void shouldDeleteVital() {

        vitalService.deleteVital(vitalId);

        verify(vitalRepository)
                .deleteById(vitalId);
    }
}