package com.pm.analyticsservice.service;

import com.pm.analyticsservice.Repository.*;
import com.pm.analyticsservice.dto.*;
import com.pm.analyticsservice.mapper.AnalyticsAppointmentMapper;
import com.pm.analyticsservice.mapper.AnalyticsDoctorMapper;
import com.pm.analyticsservice.mapper.AnalyticsPatientMapper;
import com.pm.analyticsservice.mapper.AnalyticsVitalMapper;
import com.pm.analyticsservice.model.*;
import events.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsVitalRepository repository;
    private final AnalyticsPatientRepository patientRepository;
    private final AnalyticsVitalMapper mapper;
    private final AnalyticsPatientMapper patientMapper;
    private final AnalyticsDoctorRepository doctorRepository;
    private final AnalyticsDoctorMapper doctorMapper;
    private final AnalyticsAppointmentRepository appointmentRepository;
    private final AnalyticsBillRepository billRepository;
    private final AnalyticsPrescriptionRepository prescriptionRepository;
    private final  AnalyticsDiagnosisRepository diagnosisRepository;
    private final AnalyticsInventoryRepository inventoryRepository;

    private final AnalyticsAppointmentMapper appointmentMapper;

    public List<AnalyticsVitalDTO> getAllVitals() {

        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public List<AnalyticsVitalDTO> getVitalsByPatient(
            UUID patientId) {

        return repository.findByPatientId(patientId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public AnalyticsSummaryDTO getDashboardSummary() {

        return AnalyticsSummaryDTO.builder()
                .totalVitals(repository.count())
                .averageHeartRate(
                        repository.averageHeartRate()
                )
                .averageTemperature(
                        repository.averageTemperature()
                )
                .highFeverPatients(
                        repository.countByTemperatureGreaterThan(100.4)
                )
                .lowOxygenPatients(
                        repository.countByOxygenSaturationLessThan(95)
                )
                .build();
    }

    public Long getTotalVitals() {
        return repository.count();
    }

    public Double getAverageHeartRate() {
        return repository.averageHeartRate();
    }

    public Double getAverageTemperature() {
        return repository.averageTemperature();
    }

    public Long getHighFeverCount() {
        return repository.countByTemperatureGreaterThan(100.4);
    }

    public Long getLowOxygenCount() {
        return repository.countByOxygenSaturationLessThan(95);
    }

    public void savePatient(PatientEvent event) {

        UUID patientId = UUID.fromString(event.getPatientId());

        if (patientRepository.existsById(patientId)) {
            System.out.println("Patient already exists. Ignoring duplicate create event.");
            return;
        }

        AnalyticPatient patient = AnalyticPatient.builder()
                .patientId(patientId)
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .email(event.getEmail())
                .phone(event.getPhone())
                .gender(event.getGender())
                .bloodGroup(event.getBloodGroup())
                .eventType(event.getEventType())
                .occurredAt(LocalDateTime.parse(event.getOccurredAt()))
                .build();

        patientRepository.save(patient);

        System.out.println("Patient created successfully");
    }

    public void updatePatient(PatientEvent event) {

        UUID patientId = UUID.fromString(event.getPatientId());

        AnalyticPatient patient = patientRepository.findById(patientId)
                .orElseGet(() -> AnalyticPatient.builder()
                        .patientId(patientId)
                        .build());
        
        patient.setFirstName(event.getFirstName());
        patient.setLastName(event.getLastName());
        patient.setEmail(event.getEmail());
        patient.setPhone(event.getPhone());
        patient.setGender(event.getGender());
        patient.setBloodGroup(event.getBloodGroup());

        patient.setEventType(event.getEventType());
        patient.setOccurredAt(LocalDateTime.parse(event.getOccurredAt()));

        patientRepository.save(patient);

        System.out.println("Patient updated successfully");
    }

    public void deletePatient(PatientEvent event) {

        UUID patientId = UUID.fromString(event.getPatientId());

        if (patientRepository.existsById(patientId)) {

            patientRepository.deleteById(patientId);

            System.out.println("Patient deleted successfully");

        } else {

            System.out.println("Patient already deleted or does not exist: "
                    + patientId);
        }
    }

    public List<AnalyticsPatientDTO> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toDTO)
                .toList();
    }

    public void saveDoctor(
            DoctorEvent event) {

        AnalyticsDoctor doctor =
                AnalyticsDoctor.builder()
                        .doctorId(
                                UUID.fromString(event.getDoctorId()))
                        .specialization(
                                event.getSpecialization())
                        .availabilityStatus(
                                event.getAvailabilityStatus())
                        .eventType(
                                event.getEventType())
                        .occurredAt(
                                LocalDateTime.parse(event.getOccurredAt()))
                        .build();

        doctorRepository.save(doctor);
    }

    public void updateDoctor(DoctorEvent event) {

        AnalyticsDoctor doctor = doctorRepository.findById(
                        UUID.fromString(event.getDoctorId()))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Doctor not found: " + event.getDoctorId()));

        doctor.setSpecialization(event.getSpecialization());
        doctor.setAvailabilityStatus(event.getAvailabilityStatus());
        doctor.setEventType(event.getEventType());
        doctor.setOccurredAt(LocalDateTime.parse(event.getOccurredAt()));

        doctorRepository.save(doctor);
    }

    public void deleteDoctor(DoctorEvent event) {

        UUID doctorId = UUID.fromString(event.getDoctorId());

        if (doctorRepository.existsById(doctorId)) {
            doctorRepository.deleteById(doctorId);
        }
    }

    public List<AnalyticsDoctorDTO> getAllDoctors() {

        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toDTO)
                .toList();
    }

    public void updateDoctorAvailability(
            DoctorEvent event) {

        UUID doctorId =
                UUID.fromString(event.getDoctorId());

        AnalyticsDoctor doctor =
                doctorRepository.findById(doctorId)
                        .orElseGet(() ->
                                AnalyticsDoctor.builder()
                                        .doctorId(doctorId)
                                        .build());

        if (!event.getAvailabilityStatus().isBlank()) {
            doctor.setAvailabilityStatus(
                    event.getAvailabilityStatus());
        }

        doctor.setEventType(
                event.getEventType());

        doctor.setOccurredAt(
                LocalDateTime.parse(
                        event.getOccurredAt()));

        doctorRepository.save(doctor);

        System.out.println("Doctor availability updated.");
    }

    public void saveAppointment(AppointmentEvent event) {

        AnalyticAppointment appointment =
                AnalyticAppointment.builder()
                        .appointmentId(
                                UUID.fromString(event.getAppointmentId()))
                        .patientId(
                                UUID.fromString(event.getPatientId()))
                        .doctorId(
                                UUID.fromString(event.getDoctorId()))
                        .status(event.getStatus())
                        .reason(event.getReason())
                        .appointmentDateTime(
                                LocalDateTime.parse(
                                        event.getAppointmentDateTime()))
                        .eventType(event.getEventType())
                        .occurredAt(
                                LocalDateTime.parse(
                                        event.getOccurredAt()))
                        .build();

        appointmentRepository.save(appointment);

        System.out.println("Appointment saved.");
    }

    public void updateAppointment(
            AppointmentEvent event) {

        UUID appointmentId =
                UUID.fromString(event.getAppointmentId());

        AnalyticAppointment appointment =
                appointmentRepository.findById(appointmentId)
                        .orElseGet(() ->
                                AnalyticAppointment.builder()
                                        .appointmentId(appointmentId)
                                        .build());

        if (!event.getPatientId().isBlank()) {
            appointment.setPatientId(
                    UUID.fromString(event.getPatientId()));
        }

        if (!event.getDoctorId().isBlank()) {
            appointment.setDoctorId(
                    UUID.fromString(event.getDoctorId()));
        }

        if (!event.getStatus().isBlank()) {
            appointment.setStatus(event.getStatus());
        }

        if (!event.getReason().isBlank()) {
            appointment.setReason(event.getReason());
        }

        if (!event.getAppointmentDateTime().isBlank()) {
            appointment.setAppointmentDateTime(
                    LocalDateTime.parse(
                            event.getAppointmentDateTime()));
        }

        appointment.setEventType(event.getEventType());

        appointment.setOccurredAt(
                LocalDateTime.parse(
                        event.getOccurredAt()));

        appointmentRepository.save(appointment);

        System.out.println("Appointment updated.");
    }

    public void deleteAppointment(
            AppointmentEvent event) {

        appointmentRepository.deleteById(
                UUID.fromString(event.getAppointmentId()));

        System.out.println("Appointment deleted.");
    }

    public List<AnalyticsAppointmentDTO> getAllAppointments() {

        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toDTO)
                .toList();
    }

    public List<AnalyticsAppointmentDTO> getAppointmentsByPatient(
            UUID patientId) {

        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appointmentMapper::toDTO)
                .toList();
    }

    public List<AnalyticsAppointmentDTO> getAppointmentsByDoctor(
            UUID doctorId) {

        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toDTO)
                .toList();
    }

    public List<AnalyticsAppointmentDTO> getAppointmentsByStatus(
            String status) {

        return appointmentRepository.findByStatus(status)
                .stream()
                .map(appointmentMapper::toDTO)
                .toList();
    }

    public Long getAppointmentCount() {
        return appointmentRepository.count();
    }

    public void saveBill(BillingEvent event) {

        AnalyticsBill bill =
                AnalyticsBill.builder()
                        .billId(UUID.fromString(event.getBillId()))
                        .patientId(UUID.fromString(event.getPatientId()))
                        .appointmentId(UUID.fromString(event.getAppointmentId()))
                        .amount(event.getAmount())
                        .paymentMethod(event.getPaymentMethod())
                        .paymentStatus(event.getPaymentStatus())
                        .eventType(event.getEventType())
                        .occurredAt(LocalDateTime.parse(event.getOccurredAt()))
                        .build();

        billRepository.save(bill);
    }

    public void markBillPaid(BillingEvent event) {

        AnalyticsBill bill =
                billRepository.findById(
                                UUID.fromString(event.getBillId()))
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bill not found"));

        bill.setPaymentStatus(event.getPaymentStatus());
        bill.setEventType(event.getEventType());
        bill.setOccurredAt(
                LocalDateTime.parse(event.getOccurredAt()));

        billRepository.save(bill);
    }

    public void cancelBill(BillingEvent event) {

        AnalyticsBill bill =
                billRepository.findById(
                                UUID.fromString(event.getBillId()))
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bill not found"));

        bill.setEventType(event.getEventType());
        bill.setOccurredAt(
                LocalDateTime.parse(event.getOccurredAt()));

        billRepository.save(bill);
    }

    public void saveVital(VitalsRecordedEvent event) {

        AnalyticsVital vital =
                AnalyticsVital.builder()
                        .vitalId(UUID.fromString(event.getVitalId()))
                        .patientId(UUID.fromString(event.getPatientId()))
                        .nurseId(UUID.fromString(event.getNurseId()))
                        .temperature(event.getTemperature())
                        .heartRate(event.getHeartRate())
                        .systolicBP(event.getSystolicBP())
                        .diastolicBP(event.getDiastolicBP())
                        .weight(event.getWeight())
                        .height(event.getHeight())
                        .oxygenSaturation(event.getOxygenSaturation())
                        .recordedAt(LocalDateTime.parse(event.getRecordedAt()))
                        .build();

        repository.save(vital);
    }

    public void savePrescription(
            PrescriptionEvent event) {

        AnalyticsPrescription prescription =
                AnalyticsPrescription.builder()
                        .prescriptionId(
                                UUID.fromString(event.getPrescriptionId()))
                        .patientId(
                                UUID.fromString(event.getPatientId()))
                        .doctorId(
                                UUID.fromString(event.getDoctorId()))
                        .diagnosisId(
                                UUID.fromString(event.getDiagnosisId()))
                        .appointmentId(
                                UUID.fromString(event.getAppointmentId()))
                        .medicineCount(
                                event.getMedicineCount())
                        .eventType(
                                event.getEventType())
                        .occurredAt(
                                LocalDateTime.parse(event.getOccurredAt()))
                        .build();

        prescriptionRepository.save(prescription);
    }

    public void updatePrescription(PrescriptionEvent event) {

        UUID prescriptionId =
                UUID.fromString(event.getPrescriptionId());

        AnalyticsPrescription prescription =
                prescriptionRepository.findById(prescriptionId)
                        .orElseGet(() ->
                                AnalyticsPrescription.builder()
                                        .prescriptionId(prescriptionId)
                                        .build());

        prescription.setPatientId(
                UUID.fromString(event.getPatientId()));

        prescription.setDoctorId(
                UUID.fromString(event.getDoctorId()));

        prescription.setDiagnosisId(
                UUID.fromString(event.getDiagnosisId()));

        prescription.setAppointmentId(
                UUID.fromString(event.getAppointmentId()));

        prescription.setMedicineCount(
                event.getMedicineCount());

        prescription.setEventType(
                event.getEventType());

        prescription.setOccurredAt(
                LocalDateTime.parse(event.getOccurredAt()));

        prescriptionRepository.save(prescription);
    }

    public void deletePrescription(PrescriptionEvent event) {

        prescriptionRepository.deleteById(
                UUID.fromString(event.getPrescriptionId()));
    }

    public void saveDiagnosis(DiagnosisEvent event) {

        AnalyticsDiagnosis diagnosis =
                AnalyticsDiagnosis.builder()
                        .diagnosisId(UUID.fromString(event.getDiagnosisId()))
                        .patientId(UUID.fromString(event.getPatientId()))
                        .doctorId(UUID.fromString(event.getDoctorId()))
                        .appointmentId(UUID.fromString(event.getAppointmentId()))
                        .diagnosis(event.getDiagnosis())
                        .followUpRequired(event.getFollowUpRequired())
                        .followUpDays(event.getFollowUpDays())
                        .followUpDate(
                                event.getFollowUpDate().isBlank()
                                        ? null
                                        : LocalDate.parse(event.getFollowUpDate()))
                        .eventType(event.getEventType())
                        .occurredAt(LocalDateTime.parse(event.getOccurredAt()))
                        .build();

        diagnosisRepository.save(diagnosis);
    }

    public void updateDiagnosis(DiagnosisEvent event) {

        UUID diagnosisId =
                UUID.fromString(event.getDiagnosisId());

        AnalyticsDiagnosis diagnosis =
                diagnosisRepository.findById(diagnosisId)
                        .orElseGet(() ->
                                AnalyticsDiagnosis.builder()
                                        .diagnosisId(diagnosisId)
                                        .build());

        diagnosis.setPatientId(
                UUID.fromString(event.getPatientId()));

        diagnosis.setDoctorId(
                UUID.fromString(event.getDoctorId()));

        diagnosis.setAppointmentId(
                UUID.fromString(event.getAppointmentId()));

        diagnosis.setDiagnosis(
                event.getDiagnosis());

        diagnosis.setFollowUpRequired(
                event.getFollowUpRequired());

        diagnosis.setFollowUpDays(
                event.getFollowUpDays());

        diagnosis.setFollowUpDate(
                event.getFollowUpDate().isBlank()
                        ? null
                        : LocalDate.parse(event.getFollowUpDate()));

        diagnosis.setEventType(
                event.getEventType());

        diagnosis.setOccurredAt(
                LocalDateTime.parse(event.getOccurredAt()));

        diagnosisRepository.save(diagnosis);
    }

    public void deleteDiagnosis(DiagnosisEvent event) {

        diagnosisRepository.deleteById(
                UUID.fromString(event.getDiagnosisId()));
    }

    public void saveInventory(InventoryEvent event) {

        AnalyticInventory inventory =
                AnalyticInventory.builder()
                        .inventoryId(UUID.fromString(event.getInventoryId()))
                        .itemName(event.getItemName())
                        .itemCode(event.getItemCode())
                        .quantity(event.getQuantity())
                        .minimumStock(event.getMinimumStock())
                        .unitPrice(event.getUnitPrice())
                        .status(event.getStatus())
                        .eventType(event.getEventType())
                        .occurredAt(LocalDateTime.parse(event.getOccurredAt()))
                        .build();

        inventoryRepository.save(inventory);
    }

    public void updateInventory(InventoryEvent event) {

        UUID inventoryId =
                UUID.fromString(event.getInventoryId());

        AnalyticInventory inventory =
                inventoryRepository.findById(inventoryId)
                        .orElseGet(() ->
                                AnalyticInventory.builder()
                                        .inventoryId(inventoryId)
                                        .build());

        if (!event.getItemName().isBlank()) {
            inventory.setItemName(event.getItemName());
        }

        if (!event.getItemCode().isBlank()) {
            inventory.setItemCode(event.getItemCode());
        }

        inventory.setQuantity(event.getQuantity());
        inventory.setMinimumStock(event.getMinimumStock());
        inventory.setUnitPrice(event.getUnitPrice());

        if (!event.getStatus().isBlank()) {
            inventory.setStatus(event.getStatus());
        }

        inventory.setEventType(event.getEventType());

        inventory.setOccurredAt(
                LocalDateTime.parse(event.getOccurredAt()));

        inventoryRepository.save(inventory);

        System.out.println("Inventory updated successfully.");
    }

    public void deleteInventory(InventoryEvent event) {

        inventoryRepository.deleteById(
                UUID.fromString(event.getInventoryId()));

        System.out.println("Inventory deleted successfully.");
    }
}
