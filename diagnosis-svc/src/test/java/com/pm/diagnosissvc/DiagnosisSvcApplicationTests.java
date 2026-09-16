package com.pm.diagnosissvc.service;

import com.pm.diagnosissvc.dto.DiagnosisRequestDTO;
import com.pm.diagnosissvc.dto.DiagnosisResponseDTO;
import com.pm.diagnosissvc.exception.DiagnosisNotFoundException;
import com.pm.diagnosissvc.kafka.DiagnosisProducer;
import com.pm.diagnosissvc.model.Diagnosis;
import com.pm.diagnosissvc.repository.DiagnosisRepository;
import events.DiagnosisEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagnosisServiceApplicationTests {

	private DiagnosisRepository diagnosisRepository;
	private DiagnosisProducer diagnosisProducer;
	private DiagnosisService diagnosisService;

	private UUID diagnosisId;
	private UUID patientId;
	private UUID doctorId;
	private UUID appointmentId;

	private Diagnosis diagnosis;
	private DiagnosisRequestDTO requestDTO;

	@BeforeEach
	void setUp() {

		diagnosisRepository = mock(DiagnosisRepository.class);
		diagnosisProducer = mock(DiagnosisProducer.class);

		diagnosisService =
				new DiagnosisService(
						diagnosisRepository,
						diagnosisProducer
				);

		diagnosisId = UUID.randomUUID();
		patientId = UUID.randomUUID();
		doctorId = UUID.randomUUID();
		appointmentId = UUID.randomUUID();

		diagnosis = Diagnosis.builder()
				.diagnosisId(diagnosisId)
				.patientId(patientId)
				.doctorId(doctorId)
				.appointmentId(appointmentId)
				.symptoms("Fever and cough")
				.diagnosis("Flu")
				.treatmentPlan("Rest and medication")
				.notes("Follow up if symptoms worsen")
				.followUpRequired(true)
				.followUpDays(7)
				.followUpDate(LocalDate.now().plusDays(7))
				.createdAt(LocalDateTime.now())
				.build();

		requestDTO = new DiagnosisRequestDTO();

		requestDTO.setPatientId(patientId);
		requestDTO.setDoctorId(doctorId);
		requestDTO.setAppointmentId(appointmentId);
		requestDTO.setSymptoms("Fever and cough");
		requestDTO.setDiagnosis("Flu");
		requestDTO.setTreatmentPlan("Rest and medication");
		requestDTO.setNotes("Follow up if symptoms worsen");
		requestDTO.setFollowUpDate(LocalDate.now().plusDays(7));
	}

	// ---------------------------------------------------------
	// CREATE
	// ---------------------------------------------------------

	@Test
	void createDiagnosis_shouldSaveAndReturnDiagnosis() {

		when(diagnosisRepository.save(any(Diagnosis.class)))
				.thenReturn(diagnosis);

		DiagnosisResponseDTO result =
				diagnosisService.createDiagnosis(requestDTO);

		assertNotNull(result);
		assertEquals(diagnosisId, result.getDiagnosisId());
		assertEquals(patientId, result.getPatientId());
		assertEquals(doctorId, result.getDoctorId());
		assertEquals(appointmentId, result.getAppointmentId());
		assertEquals("Fever and cough", result.getSymptoms());
		assertEquals("Flu", result.getDiagnosis());

		verify(diagnosisRepository).save(any(Diagnosis.class));
		verify(diagnosisProducer)
				.publishDiagnosisCreated(any(DiagnosisEvent.class));
	}

	@Test
	void createDiagnosis_shouldPublishCorrectEvent() {

		when(diagnosisRepository.save(any(Diagnosis.class)))
				.thenReturn(diagnosis);

		diagnosisService.createDiagnosis(requestDTO);

		ArgumentCaptor<DiagnosisEvent> captor =
				ArgumentCaptor.forClass(DiagnosisEvent.class);

		verify(diagnosisProducer)
				.publishDiagnosisCreated(captor.capture());

		DiagnosisEvent event = captor.getValue();

		assertEquals(
				diagnosisId.toString(),
				event.getDiagnosisId()
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
				appointmentId.toString(),
				event.getAppointmentId()
		);

		assertEquals(
				"Flu",
				event.getDiagnosis()
		);

		assertTrue(event.getFollowUpRequired());

		assertEquals(
				7,
				event.getFollowUpDays()
		);

		assertEquals(
				diagnosis.getFollowUpDate().toString(),
				event.getFollowUpDate()
		);

		assertEquals(
				"DIAGNOSIS_CREATED",
				event.getEventType()
		);
	}

	// ---------------------------------------------------------
	// GET BY ID
	// ---------------------------------------------------------

	@Test
	void getDiagnosis_shouldReturnDiagnosis() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.of(diagnosis));

		DiagnosisResponseDTO result =
				diagnosisService.getDiagnosis(diagnosisId);

		assertNotNull(result);
		assertEquals(diagnosisId, result.getDiagnosisId());
		assertEquals("Flu", result.getDiagnosis());

		verify(diagnosisRepository)
				.findById(diagnosisId);
	}

	@Test
	void getDiagnosis_shouldThrowWhenNotFound() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.empty());

		assertThrows(
				DiagnosisNotFoundException.class,
				() -> diagnosisService.getDiagnosis(diagnosisId)
		);

		verify(diagnosisRepository)
				.findById(diagnosisId);
	}

	// ---------------------------------------------------------
	// GET ALL
	// ---------------------------------------------------------

	@Test
	void getDiagnoses_shouldReturnAllDiagnoses() {

		Diagnosis secondDiagnosis =
				Diagnosis.builder()
						.diagnosisId(UUID.randomUUID())
						.patientId(patientId)
						.doctorId(doctorId)
						.appointmentId(appointmentId)
						.symptoms("Headache")
						.diagnosis("Migraine")
						.treatmentPlan("Pain relief")
						.notes("Monitor symptoms")
						.followUpRequired(false)
						.followUpDays(0)
						.createdAt(LocalDateTime.now())
						.build();

		when(diagnosisRepository.findAll())
				.thenReturn(List.of(diagnosis, secondDiagnosis));

		List<DiagnosisResponseDTO> result =
				diagnosisService.getDiagnoses();

		assertEquals(2, result.size());

		assertEquals(
				"Flu",
				result.get(0).getDiagnosis()
		);

		assertEquals(
				"Migraine",
				result.get(1).getDiagnosis()
		);

		verify(diagnosisRepository).findAll();
	}

	@Test
	void getDiagnoses_shouldReturnEmptyListWhenNoDiagnoses() {

		when(diagnosisRepository.findAll())
				.thenReturn(List.of());

		List<DiagnosisResponseDTO> result =
				diagnosisService.getDiagnoses();

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(diagnosisRepository).findAll();
	}

	// ---------------------------------------------------------
	// GET BY PATIENT
	// ---------------------------------------------------------

	@Test
	void getDiagnosesByPatient_shouldReturnPatientDiagnoses() {

		when(diagnosisRepository.findByPatientId(patientId))
				.thenReturn(List.of(diagnosis));

		List<DiagnosisResponseDTO> result =
				diagnosisService.getDiagnosesByPatient(patientId);

		assertEquals(1, result.size());

		assertEquals(
				diagnosisId,
				result.get(0).getDiagnosisId()
		);

		assertEquals(
				patientId,
				result.get(0).getPatientId()
		);

		verify(diagnosisRepository)
				.findByPatientId(patientId);
	}

	@Test
	void getDiagnosesByPatient_shouldReturnEmptyList() {

		when(diagnosisRepository.findByPatientId(patientId))
				.thenReturn(List.of());

		List<DiagnosisResponseDTO> result =
				diagnosisService.getDiagnosesByPatient(patientId);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(diagnosisRepository)
				.findByPatientId(patientId);
	}

	// ---------------------------------------------------------
	// GET BY DOCTOR
	// ---------------------------------------------------------

	@Test
	void getDiagnosesByDoctor_shouldReturnDoctorDiagnoses() {

		when(diagnosisRepository.findByDoctorId(doctorId))
				.thenReturn(List.of(diagnosis));

		List<DiagnosisResponseDTO> result =
				diagnosisService.getDiagnosesByDoctor(doctorId);

		assertEquals(1, result.size());

		assertEquals(
				diagnosisId,
				result.get(0).getDiagnosisId()
		);

		assertEquals(
				doctorId,
				result.get(0).getDoctorId()
		);

		verify(diagnosisRepository)
				.findByDoctorId(doctorId);
	}

	@Test
	void getDiagnosesByDoctor_shouldReturnEmptyList() {

		when(diagnosisRepository.findByDoctorId(doctorId))
				.thenReturn(List.of());

		List<DiagnosisResponseDTO> result =
				diagnosisService.getDiagnosesByDoctor(doctorId);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(diagnosisRepository)
				.findByDoctorId(doctorId);
	}

	// ---------------------------------------------------------
	// UPDATE
	// ---------------------------------------------------------

	@Test
	void updateDiagnosis_shouldUpdateFieldsAndPublishEvent() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.of(diagnosis));

		when(diagnosisRepository.save(any(Diagnosis.class)))
				.thenReturn(diagnosis);

		DiagnosisRequestDTO updateDTO =
				new DiagnosisRequestDTO();

		updateDTO.setPatientId(patientId);
		updateDTO.setDoctorId(doctorId);
		updateDTO.setAppointmentId(appointmentId);
		updateDTO.setSymptoms("Severe fever");
		updateDTO.setDiagnosis("Viral infection");
		updateDTO.setTreatmentPlan("Medication and rest");
		updateDTO.setNotes("Review after one week");
		updateDTO.setFollowUpDate(
				LocalDate.now().plusDays(10)
		);

		DiagnosisResponseDTO result =
				diagnosisService.updateDiagnosis(
						diagnosisId,
						updateDTO
				);

		assertNotNull(result);

		assertEquals(
				"Severe fever",
				diagnosis.getSymptoms()
		);

		assertEquals(
				"Viral infection",
				diagnosis.getDiagnosis()
		);

		assertEquals(
				"Medication and rest",
				diagnosis.getTreatmentPlan()
		);

		assertEquals(
				"Review after one week",
				diagnosis.getNotes()
		);

		assertEquals(
				updateDTO.getFollowUpDate(),
				diagnosis.getFollowUpDate()
		);

		verify(diagnosisRepository)
				.findById(diagnosisId);

		verify(diagnosisRepository)
				.save(diagnosis);

		verify(diagnosisProducer)
				.publishDiagnosisUpdated(
						any(DiagnosisEvent.class)
				);
	}

	@Test
	void updateDiagnosis_shouldPublishCorrectEvent() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.of(diagnosis));

		when(diagnosisRepository.save(any(Diagnosis.class)))
				.thenReturn(diagnosis);

		diagnosisService.updateDiagnosis(
				diagnosisId,
				requestDTO
		);

		ArgumentCaptor<DiagnosisEvent> captor =
				ArgumentCaptor.forClass(DiagnosisEvent.class);

		verify(diagnosisProducer)
				.publishDiagnosisUpdated(captor.capture());

		DiagnosisEvent event = captor.getValue();

		assertEquals(
				diagnosisId.toString(),
				event.getDiagnosisId()
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
				appointmentId.toString(),
				event.getAppointmentId()
		);

		assertEquals(
				"Flu",
				event.getDiagnosis()
		);

		assertEquals(
				"DIAGNOSIS_UPDATED",
				event.getEventType()
		);
	}

	@Test
	void updateDiagnosis_shouldThrowWhenNotFound() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.empty());

		assertThrows(
				DiagnosisNotFoundException.class,
				() -> diagnosisService.updateDiagnosis(
						diagnosisId,
						requestDTO
				)
		);

		verify(diagnosisRepository)
				.findById(diagnosisId);

		verify(diagnosisRepository, never())
				.save(any(Diagnosis.class));

		verifyNoInteractions(diagnosisProducer);
	}

	// ---------------------------------------------------------
	// DELETE
	// ---------------------------------------------------------

	@Test
	void deleteDiagnosis_shouldPublishAndDelete() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.of(diagnosis));

		diagnosisService.deleteDiagnosis(diagnosisId);

		verify(diagnosisProducer)
				.publishDiagnosisDeleted(
						any(DiagnosisEvent.class)
				);

		verify(diagnosisRepository)
				.delete(diagnosis);
	}

	@Test
	void deleteDiagnosis_shouldPublishCorrectEvent() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.of(diagnosis));

		diagnosisService.deleteDiagnosis(diagnosisId);

		ArgumentCaptor<DiagnosisEvent> captor =
				ArgumentCaptor.forClass(DiagnosisEvent.class);

		verify(diagnosisProducer)
				.publishDiagnosisDeleted(captor.capture());

		DiagnosisEvent event = captor.getValue();

		assertEquals(
				diagnosisId.toString(),
				event.getDiagnosisId()
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
				appointmentId.toString(),
				event.getAppointmentId()
		);

		assertEquals(
				"Flu",
				event.getDiagnosis()
		);

		assertEquals(
				"DIAGNOSIS_DELETED",
				event.getEventType()
		);
	}

	@Test
	void deleteDiagnosis_shouldThrowWhenNotFound() {

		when(diagnosisRepository.findById(diagnosisId))
				.thenReturn(Optional.empty());

		assertThrows(
				DiagnosisNotFoundException.class,
				() -> diagnosisService.deleteDiagnosis(diagnosisId)
		);

		verify(diagnosisRepository)
				.findById(diagnosisId);

		verify(diagnosisRepository, never())
				.delete(any(Diagnosis.class));

		verifyNoInteractions(diagnosisProducer);
	}
}