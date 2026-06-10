package com.pm.billingservice.mapper;

import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.model.Bill;

public class BillingMapper {

    private BillingMapper() {
    }

    public static Bill toEntity(
            BillingRequestDTO dto) {

        return Bill.builder()
                .patientId(dto.getPatientId())
                .appointmentId(dto.getAppointmentId())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .build();
    }

    public static BillingResponseDTO toDTO(
            Bill bill) {

        return BillingResponseDTO.builder()
                .billId(bill.getBillId())
                .patientId(bill.getPatientId())
                .appointmentId(bill.getAppointmentId())
                .amount(bill.getAmount())
                .paymentMethod(bill.getPaymentMethod())
                .paymentStatus(bill.getPaymentStatus())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }
}