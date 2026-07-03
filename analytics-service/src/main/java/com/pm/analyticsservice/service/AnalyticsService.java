package com.pm.analyticsservice.service;

import com.pm.analyticsservice.Repository.AnalyticsAppointmentRepository;
import com.pm.analyticsservice.Repository.AnalyticsDoctorRepository;
import com.pm.analyticsservice.Repository.AnalyticsPatientRepository;
import com.pm.analyticsservice.Repository.AnalyticsVitalRepository;
import com.pm.analyticsservice.dto.*;
import com.pm.analyticsservice.mapper.AnalyticsAppointmentMapper;
import com.pm.analyticsservice.mapper.AnalyticsDoctorMapper;
import com.pm.analyticsservice.mapper.AnalyticsPatientMapper;
import com.pm.analyticsservice.mapper.AnalyticsVitalMapper;
import com.pm.analyticsservice.model.AnalyticAppointment;
import com.pm.analyticsservice.model.AnalyticPatient;
import com.pm.analyticsservice.model.AnalyticsDoctor;
import com.pm.analyticsservice.model.AnalyticsVital;
import events.AppointmentEvent;
import events.DoctorEvent;
import events.PatientEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    //    public void saveVital(
//            VitalsRecordedEvent event) {
//
//        AnalyticsVital vital =
//                AnalyticsVital.builder()
//                        .vitalId(event.getVitalId())
//                        .patientId(event.getPatientId())
//                        .nurseId(event.getNurseId())
//                        .temperature(event.getTemperature())
//                        .heartRate(event.getHeartRate())
//                        .systolicBP(event.getSystolicBP())
//                        .diastolicBP(event.getDiastolicBP())
//                        .weight(event.getWeight())
//                        .height(event.getHeight())
//                        .oxygenSaturation(event.getOxygenSaturation())
//                        .recordedAt(event.getRecordedAt())
//                        .build();
//
//        repository.save(vital);
//    }
//
    public void savePatient(PatientEvent event) {

        UUID patientId = UUID.fromString(event.getPatientId());

        if (patientRepository.existsById(patientId)) {
            System.out.println("Patient already exists. Ignoring duplicate create event.");
            return;
        }

        AnalyticPatient patient = AnalyticPatient.builder()
                .patientId(patientId)
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

        if (!event.getGender().isBlank()) {
            patient.setGender(event.getGender());
        }

        if (!event.getBloodGroup().isBlank()) {
            patient.setBloodGroup(event.getBloodGroup());
        }

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
}
