package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(
            PatientService patientService) {

        this.patientService = patientService;
    }

    @GetMapping("/test")
    public String test(
            @RequestHeader("X-User-Id")
            String userId,

            @RequestHeader("X-Role")
            String role,

            @RequestHeader("X-Email")
            String email) {

        return userId + " "
                + role + " "
                + email;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>>
    getPatients() {

        return ResponseEntity.ok(
                patientService.getPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO>
    getPatient(@PathVariable UUID id) {

        return ResponseEntity.ok(
                patientService.getPatient(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDTO>>
    searchPatients(
            @RequestParam String name) {

        return ResponseEntity.ok(
                patientService.searchPatients(name));
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO>
    createPatient(
            @Valid
            @RequestBody PatientRequestDTO dto) {

        return ResponseEntity.ok(
                patientService.createPatient(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO>
    updatePatient(
            @PathVariable UUID id,
            @Valid
            @RequestBody PatientRequestDTO dto) {

        return ResponseEntity.ok(
                patientService.updatePatient(id, dto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void>
    deactivatePatient(
            @PathVariable UUID id) {

        patientService.deactivatePatient(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return null;
    }
}