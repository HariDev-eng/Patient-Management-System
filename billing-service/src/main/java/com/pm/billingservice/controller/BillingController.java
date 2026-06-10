package com.pm.billingservice.controller;

import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.enums.PaymentStatus;
import com.pm.billingservice.service.BillingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bills")
@AllArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<BillingResponseDTO> createBill(
            @Valid @RequestBody BillingRequestDTO request) {

        return ResponseEntity.ok(
                billingService.createBill(request));
    }

    @GetMapping
    public ResponseEntity<List<BillingResponseDTO>> getAllBills() {

        return ResponseEntity.ok(
                billingService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingResponseDTO> getBillById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                billingService.getBillById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillingResponseDTO>>
    getBillsByPatientId(
            @PathVariable UUID patientId) {

        return ResponseEntity.ok(
                billingService.getBillsByPatientId(patientId));
    }

    @GetMapping("/status")
    public ResponseEntity<List<BillingResponseDTO>>
    getBillsByStatus(
            @RequestParam PaymentStatus status) {

        return ResponseEntity.ok(
                billingService.getBillsByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BillingResponseDTO>
    updatePaymentStatus(
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {

        return ResponseEntity.ok(
                billingService.updatePaymentStatus(
                        id,
                        status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(
            @PathVariable UUID id) {

        billingService.deleteBill(id);

        return ResponseEntity.noContent().build();
    }
}