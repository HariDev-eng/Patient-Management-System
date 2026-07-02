package com.pm.analyticsservice.controller;

import com.pm.analyticsservice.dto.AnalyticsPatientDTO;
import com.pm.analyticsservice.dto.AnalyticsSummaryDTO;
import com.pm.analyticsservice.dto.AnalyticsVitalDTO;
import com.pm.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public AnalyticsSummaryDTO getDashboard() {

        return analyticsService.getDashboardSummary();
    }

    @GetMapping("/vitals")
    public List<AnalyticsVitalDTO> getAllVitals() {

        return analyticsService.getAllVitals();
    }

    @GetMapping("/vitals/patient/{patientId}")
    public List<AnalyticsVitalDTO> getVitalsByPatient(
            @PathVariable UUID patientId) {

        return analyticsService.getVitalsByPatient(patientId);
    }

    @GetMapping("/vitals/count")
    public Long totalVitals() {

        return analyticsService.getTotalVitals();
    }

    @GetMapping("/vitals/average-heart-rate")
    public Double averageHeartRate() {

        return analyticsService.getAverageHeartRate();
    }

    @GetMapping("/vitals/average-temperature")
    public Double averageTemperature() {

        return analyticsService.getAverageTemperature();
    }

    @GetMapping("/vitals/high-fever")
    public Long highFeverPatients() {

        return analyticsService.getHighFeverCount();
    }

    @GetMapping("/vitals/low-oxygen")
    public Long lowOxygenPatients() {

        return analyticsService.getLowOxygenCount();
    }

    @GetMapping("/patients")
    public List<AnalyticsPatientDTO> getAllPatients(){

        return analyticsService.getAllPatients();
    }
}