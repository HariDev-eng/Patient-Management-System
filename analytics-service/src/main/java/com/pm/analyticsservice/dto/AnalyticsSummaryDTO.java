package com.pm.analyticsservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDTO {

    private Long totalVitals;

    private Double averageHeartRate;

    private Double averageTemperature;

    private Long highFeverPatients;

    private Long lowOxygenPatients;
}