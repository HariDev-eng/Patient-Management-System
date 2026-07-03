package com.pm.analyticsservice.mapper;

import com.pm.analyticsservice.dto.AnalyticsDoctorDTO;
import com.pm.analyticsservice.model.AnalyticsDoctor;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsDoctorMapper {

    public AnalyticsDoctorDTO toDTO(
            AnalyticsDoctor doctor){

        return AnalyticsDoctorDTO.builder()
                .doctorId(doctor.getDoctorId())
                .specialization(doctor.getSpecialization())
                .availabilityStatus(doctor.getAvailabilityStatus())
                .eventType(doctor.getEventType())
                .occurredAt(doctor.getOccurredAt())
                .build();
    }
}
