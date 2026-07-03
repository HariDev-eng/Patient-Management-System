package com.pm.appointmentservice.service;


import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.enums.AppointmentStatus;
import com.pm.appointmentservice.grpc.PatientGrpcClient;
import com.pm.appointmentservice.kafka.AppointmentProducer;
import com.pm.appointmentservice.mapper.AppointmentMapper;
import com.pm.appointmentservice.model.Appointment;
import com.pm.appointmentservice.repository.AppointmentRepository;
import events.AppointmentEvent;
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
    private final AppointmentProducer appointmentProducer;

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

        AppointmentEvent event =
                AppointmentEvent.newBuilder()
                        .setAppointmentId(saved.getAppointmentId().toString())
                        .setPatientId(saved.getPatientId().toString())
                        .setDoctorId(saved.getDoctorId().toString())
                        .setAppointmentDateTime(saved.getAppointmentDateTime().toString())
                        .setReason(saved.getReason())
                        .setStatus(saved.getStatus().name())
                        .setEventType("APPOINTMENT_CREATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        appointmentProducer.publishAppointmentCreated(event);


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
        Appointment updated = appointmentRepository.save(appointment);

        AppointmentEvent event =
                AppointmentEvent.newBuilder()
                        .setAppointmentId(updated.getAppointmentId().toString())
                        .setPatientId(updated.getPatientId().toString())
                        .setDoctorId(updated.getDoctorId().toString())
                        .setAppointmentDateTime(updated.getAppointmentDateTime().toString())
                        .setReason(updated.getReason())
                        .setStatus(updated.getStatus().name())
                        .setEventType("APPOINTMENT_UPDATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        appointmentProducer.publishAppointmentUpdated(event);

        return AppointmentMapper.toDTO(updated);
    }

    public void deleteAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found with id: " + appointmentId));

        AppointmentEvent event =
                AppointmentEvent.newBuilder()
                        .setAppointmentId(appointment.getAppointmentId().toString())
                        .setEventType("APPOINTMENT_DELETED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        appointmentProducer.publishAppointmentDeleted(event);

        appointmentRepository.delete(appointment);
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
