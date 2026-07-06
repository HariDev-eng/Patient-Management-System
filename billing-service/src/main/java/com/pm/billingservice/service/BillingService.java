package com.pm.billingservice.service;

import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.enums.PaymentStatus;
import com.pm.billingservice.kafka.BillingProducer;
import com.pm.billingservice.mapper.BillingMapper;
import com.pm.billingservice.model.Bill;
import com.pm.billingservice.repository.BillingRepository;
import events.BillingEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BillingService {

    private final BillingRepository billingRepository;
    private final BillingProducer billingProducer;

    public BillingResponseDTO createBill(
            BillingRequestDTO dto) {

        Bill bill =
                BillingMapper.toEntity(dto);

        bill.setPaymentStatus(
                PaymentStatus.PENDING);

        Bill saved =
                billingRepository.save(bill);

        BillingEvent event =
                BillingEvent.newBuilder()
                        .setBillId(saved.getBillId().toString())
                        .setAppointmentId(saved.getAppointmentId().toString())
                        .setPatientId(saved.getPatientId().toString())
                        .setAmount(saved.getAmount().doubleValue())
                        .setPaymentStatus(saved.getPaymentStatus().name())
                        .setPaymentMethod(saved.getPaymentMethod())
                        .setEventType("BILL_CREATED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        billingProducer.publishBillCreated(event);

        return BillingMapper.toDTO(saved);
    }

    public BillingResponseDTO getBillById(
            UUID billId) {

        Bill bill =
                billingRepository.findById(billId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bill not found with id: "
                                                + billId));

        return BillingMapper.toDTO(bill);
    }

    public List<BillingResponseDTO> getAllBills() {

        return billingRepository.findAll()
                .stream()
                .map(BillingMapper::toDTO)
                .toList();
    }

    public List<BillingResponseDTO> getBillsByPatientId(
            UUID patientId) {

        return billingRepository.findByPatientId(patientId)
                .stream()
                .map(BillingMapper::toDTO)
                .toList();
    }

    public List<BillingResponseDTO> getBillsByStatus(
            PaymentStatus status) {

        return billingRepository.findByPaymentStatus(status)
                .stream()
                .map(BillingMapper::toDTO)
                .toList();
    }

    public BillingResponseDTO updatePaymentStatus(
            UUID billId,
            PaymentStatus status) {

        Bill bill =
                billingRepository.findById(billId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bill not found with id: "
                                                + billId));

        bill.setPaymentStatus(status);

        Bill updated =
                billingRepository.save(bill);

        BillingEvent event =
                BillingEvent.newBuilder()
                        .setBillId(updated.getBillId().toString())
                        .setAppointmentId(updated.getAppointmentId().toString())
                        .setPatientId(updated.getPatientId().toString())
                        .setAmount(updated.getAmount())
                        .setPaymentMethod(updated.getPaymentMethod())
                        .setPaymentStatus(updated.getPaymentStatus().name())
                        .setEventType("BILL_PAID")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        billingProducer.publishBillPaid(event);

        return BillingMapper.toDTO(updated);
    }

    public void deleteBill(
            UUID billId) {

        Bill bill =
                billingRepository.findById(billId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bill not found with id: "
                                                + billId));

        billingRepository.delete(bill);

        BillingEvent event =
                BillingEvent.newBuilder()
                        .setBillId(bill.getBillId().toString())
                        .setPatientId(bill.getPatientId().toString())
                        .setAppointmentId(bill.getAppointmentId().toString())
                        .setEventType("BILL_DELETED")
                        .setOccurredAt(LocalDateTime.now().toString())
                        .build();

        billingProducer.publishBillDeleted(event);

        billingRepository.delete(bill);
    }
//
//    public void createBillingFromAppointment(
//            AppointmentEv event) {
//
//        Bill billing = new Bill();
//
//        billing.setPatientId(
//                UUID.fromString(event.getPatientId()));
//
//        billing.setAppointmentId(
//                UUID.fromString(event.getAppointmentId()));
//
//        billing.setAmount(1000.0);
//
//        billing.setPaymentStatus(
//                PaymentStatus.PENDING);
//
//        billingRepository.save(billing);
//    }
}