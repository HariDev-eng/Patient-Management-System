package com.pm.analyticsservice.controller;

import com.pm.analyticsservice.dto.*;
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

    @GetMapping("/doctors")
    public List<AnalyticsDoctorDTO> getAllDoctors(){

        return analyticsService.getAllDoctors();
    }

    @GetMapping("/appointments")
    public List<AnalyticsAppointmentDTO> getAppointments() {

        return analyticsService.getAllAppointments();
    }

    @GetMapping("/appointments/patient/{patientId}")
    public List<AnalyticsAppointmentDTO> getAppointmentsByPatient(
            @PathVariable UUID patientId) {

        return analyticsService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/appointments/doctor/{doctorId}")
    public List<AnalyticsAppointmentDTO> getAppointmentsByDoctor(
            @PathVariable UUID doctorId) {

        return analyticsService.getAppointmentsByDoctor(doctorId);
    }

    @GetMapping("/appointments/status/{status}")
    public List<AnalyticsAppointmentDTO> getAppointmentsByStatus(
            @PathVariable String status) {

        return analyticsService.getAppointmentsByStatus(status);
    }

    @GetMapping("/appointments/count")
    public Long totalAppointments() {

        return analyticsService.getAppointmentCount();
    }
}