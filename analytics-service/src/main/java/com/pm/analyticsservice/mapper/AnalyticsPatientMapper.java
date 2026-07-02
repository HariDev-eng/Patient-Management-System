package com.pm.analyticsservice.mapper;

import com.pm.analyticsservice.dto.AnalyticsPatientDTO;
import com.pm.analyticsservice.model.AnalyticPatient;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsPatientMapper {

    public AnalyticsPatientDTO toDTO(AnalyticPatient patient){

        return AnalyticsPatientDTO.builder()
                .patientId(patient.getPatientId())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .eventType(patient.getEventType())
                .occurredAt(patient.getOccurredAt())
                .build();
    }

}