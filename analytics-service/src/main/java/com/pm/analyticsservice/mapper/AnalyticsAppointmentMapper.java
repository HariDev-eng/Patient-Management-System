package com.pm.analyticsservice.mapper;

import com.pm.analyticsservice.dto.AnalyticsAppointmentDTO;
import com.pm.analyticsservice.model.AnalyticAppointment;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsAppointmentMapper {

    public AnalyticsAppointmentDTO toDTO(
            AnalyticAppointment appointment) {

        return AnalyticsAppointmentDTO.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .appointmentDateTime(
                        appointment.getAppointmentDateTime())
                .eventType(appointment.getEventType())
                .occurredAt(appointment.getOccurredAt())
                .build();
    }
}