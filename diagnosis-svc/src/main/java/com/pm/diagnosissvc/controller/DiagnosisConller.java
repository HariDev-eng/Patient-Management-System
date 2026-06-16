package com.pm.diagnosissvc.controller;

import com.pm.diagnosissvc.dto.DiagnosisRequestDTO;
import com.pm.diagnosissvc.dto.DiagnosisResponseDTO;
import com.pm.diagnosissvc.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/diagnoses")
@AllArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    public ResponseEntity<DiagnosisResponseDTO>
    createDiagnosis(
            @Valid
            @RequestBody DiagnosisRequestDTO dto) {

        return ResponseEntity.ok(
                diagnosisService.createDiagnosis(dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisResponseDTO>>
    getDiagnoses() {

        return ResponseEntity.ok(
                diagnosisService.getDiagnoses()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisResponseDTO>
    getDiagnosis(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                diagnosisService.getDiagnosis(id)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DiagnosisResponseDTO>>
    getDiagnosesByPatient(
            @PathVariable UUID patientId) {

        return ResponseEntity.ok(
                diagnosisService
                        .getDiagnosesByPatient(patientId)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DiagnosisResponseDTO>>
    getDiagnosesByDoctor(
            @PathVariable UUID doctorId) {

        return ResponseEntity.ok(
                diagnosisService
                        .getDiagnosesByDoctor(doctorId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosisResponseDTO>
    updateDiagnosis(
            @PathVariable UUID id,
            @Valid
            @RequestBody DiagnosisRequestDTO dto) {

        return ResponseEntity.ok(
                diagnosisService.updateDiagnosis(
                        id,
                        dto
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteDiagnosis(
            @PathVariable UUID id) {

        diagnosisService.deleteDiagnosis(id);

        return ResponseEntity.noContent().build();
    }
}