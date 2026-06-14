package com.pm.appointmentservice.service;


import appointment.events.AppointmentCreatedEvent;
import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.enums.AppointmentStatus;
import com.pm.appointmentservice.grpc.PatientGrpcClient;
import com.pm.appointmentservice.kafka.AppointmentEventProducer;
import com.pm.appointmentservice.mapper.AppointmentMapper;
import com.pm.appointmentservice.model.Appointment;
import com.pm.appointmentservice.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientGrpcClient patientGrpcClient;
    private final AppointmentEventProducer producer;

    public AppointmentResponseDTO createAppointment(
            AppointmentRequestDTO dto) {
        if(dto.getAppointmentDateTime()
                .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Appointment cannot be scheduled in the past");
        }
        if (!patientGrpcClient.patientExists(dto.getPatientId())) {
            throw new RuntimeException("Patient not found");
        }

        Appointment appointment = AppointmentMapper.toEntity(dto);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Appointment saved = appointmentRepository.save(appointment);

        AppointmentCreatedEvent event =
                AppointmentCreatedEvent.newBuilder()
                        .setAppointmentId(
                                saved.getAppointmentId().toString())
                        .setPatientId(
                                saved.getPatientId().toString())
                        .setDoctorId(
                                saved.getDoctorId().toString())
                        .setAppointmentDateTime(
                                saved.getAppointmentDateTime().toString())
                        .setReason(
                                saved.getReason())
                        .build();

        producer.publishAppointmentCreated(event);
        return AppointmentMapper.toDTO(saved);
    }

    public AppointmentResponseDTO getAppointmentById(
            UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found with id: " + appointmentId));
        return AppointmentMapper.toDTO(appointment);
    }

    public AppointmentResponseDTO updateAppointmentStatus(UUID appointmentId,
                                        AppointmentStatus status, AppointmentRequestDTO requestDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found with id: " + appointmentId));
        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setReason(requestDTO.getReason());
        appointment.setNotes(requestDTO.getNotes());
        appointmentRepository.save(appointment);
        return AppointmentMapper.toDTO(appointment);
    }

    public void deleteAppointment(UUID appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new RuntimeException(
                    "Appointment not found with id: " + appointmentId);
        }
        appointmentRepository.deleteById(appointmentId);
    }

    public List<AppointmentResponseDTO> getAppointmentsByPatientId(
            UUID patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(AppointmentMapper::toDTO)
                .toList();
    }

    public List<AppointmentResponseDTO> getAppointmentsByDoctorId(
            UUID doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(AppointmentMapper::toDTO)
                .toList();
    }

    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentMapper::toDTO)
                .toList();
    }

    public List<AppointmentResponseDTO> getAppointmentsByStatus(
            AppointmentStatus status) {

        return appointmentRepository
                .findByStatus(status)
                .stream()
                .map(AppointmentMapper::toDTO)
                .toList();
    }
}
