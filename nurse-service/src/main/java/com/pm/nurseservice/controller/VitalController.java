package com.pm.nurseservice.controller;

import com.pm.nurseservice.dto.VitalRequestDTO;
import com.pm.nurseservice.dto.VitalResponseDTO;
import com.pm.nurseservice.service.VitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vitals")
@RequiredArgsConstructor
public class VitalController {

    private final VitalService vitalService;

    @PostMapping
    public ResponseEntity<VitalResponseDTO>
    createVital(
            @RequestBody
            VitalRequestDTO dto) {

        return ResponseEntity.ok(
                vitalService.createVital(dto)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VitalResponseDTO>
    getVital(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                vitalService.getVitalById(id)
        );
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VitalResponseDTO>>
    getPatientVitals(
            @PathVariable UUID patientId) {

        return ResponseEntity.ok(
                vitalService.getVitalsByPatient(
                        patientId
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteVital(
            @PathVariable UUID id) {

        vitalService.deleteVital(id);

        return ResponseEntity.noContent()
                .build();
    }
}