package com.pm.doctorsvc.controller;


import com.pm.doctorsvc.dto.DoctorRequestDTO;
import com.pm.doctorsvc.dto.DoctorResponseDTO;
import com.pm.doctorsvc.enums.AvailabilityStatus;
import com.pm.doctorsvc.enums.Specialization;
import com.pm.doctorsvc.service.DoctorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/doctors")
@AllArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(@Valid @RequestBody DoctorRequestDTO doctorRequestDTO) {
        return ResponseEntity.ok(doctorService.createDoctor(doctorRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getDoctors() {
        return ResponseEntity.ok(doctorService.getDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable UUID id, @Valid @RequestBody DoctorRequestDTO doctorRequestDTO) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<DoctorResponseDTO> updateAvailabilityStatus(
            @PathVariable UUID id,
            @RequestParam AvailabilityStatus status) {

        DoctorResponseDTO doctor =
                doctorService.updateAvailability(id, status);

        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(
            @RequestParam Specialization specialization) {

        return ResponseEntity.ok(
                doctorService.searchBySpecialization(specialization)
        );
    }
}
