package com.pm.appointmentservice.mapper;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.model.Appointment;

public class AppointmentMapper {

    private AppointmentMapper() {
        // Private constructor to prevent instantiation
    }

    public static Appointment toEntity(AppointmentRequestDTO dto) {

        return Appointment.builder().doctorId(dto.getDoctorId())
                .patientId(dto.getPatientId())
                .appointmentDateTime(dto.getAppointmentDateTime())
                .reason(dto.getReason())
                .notes(dto.getNotes())
                .build();
    }

    public static AppointmentResponseDTO toDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getAppointmentId())
                .doctorId(appointment.getDoctorId())
                .patientId(appointment.getPatientId())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .build();
    }
}
