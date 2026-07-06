package src.main.java.com.pm.prescriptiosvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionRequestDTO;
import src.main.java.com.pm.prescriptiosvc.dto.PrescriptionResponseDTO;
import src.main.java.com.pm.prescriptiosvc.service.PrescriptionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    @PostMapping
    public ResponseEntity<PrescriptionResponseDTO>
    createPrescription(
            @RequestBody
            PrescriptionRequestDTO dto) {

        return ResponseEntity.ok(
                service.createPrescription(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponseDTO>>
    getAllPrescriptions() {

        return ResponseEntity.ok(
                service.getAllPrescriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDTO>
    getPrescription(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                service.getPrescription(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponseDTO>>
    getPatientPrescriptions(
            @PathVariable UUID patientId) {

        return ResponseEntity.ok(
                service.getPatientPrescriptions(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionResponseDTO>
    updatePrescription(
            @PathVariable UUID id,
            @RequestBody PrescriptionRequestDTO dto) {

        return ResponseEntity.ok(
                service.updatePrescription(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deletePrescription(
            @PathVariable UUID id) {

        service.deletePrescription(id);

        return ResponseEntity.noContent().build();
    }
}