package com.pm.billingservice;

import com.pm.billingservice.dto.BillingRequestDTO;
import com.pm.billingservice.dto.BillingResponseDTO;
import com.pm.billingservice.enums.PaymentStatus;
import com.pm.billingservice.kafka.BillingProducer;
import com.pm.billingservice.model.Bill;
import com.pm.billingservice.repository.BillingRepository;
import com.pm.billingservice.service.BillingService;
import events.BillingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BillingServiceApplicationTests {

    private BillingRepository billingRepository;
    private BillingProducer billingProducer;
    private BillingService billingService;

    private UUID billId;
    private UUID patientId;
    private UUID appointmentId;

    private Bill bill;
    private BillingRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        billingRepository = mock(BillingRepository.class);
        billingProducer = mock(BillingProducer.class);

        billingService =
                new BillingService(
                        billingRepository,
                        billingProducer
                );

        billId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        bill = Bill.builder()
                .billId(billId)
                .patientId(patientId)
                .appointmentId(appointmentId)
                .amount(1500.0)
                .paymentMethod("UPI")
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        requestDTO = BillingRequestDTO.builder()
                .patientId(patientId)
                .appointmentId(appointmentId)
                .amount(1500.0)
                .paymentMethod("UPI")
                .build();
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void createBill_shouldSaveAndReturnBill() {

        when(billingRepository.save(any(Bill.class)))
                .thenReturn(bill);

        BillingResponseDTO result =
                billingService.createBill(requestDTO);

        assertNotNull(result);

        assertEquals(
                billId,
                result.getBillId()
        );

        assertEquals(
                patientId,
                result.getPatientId()
        );

        assertEquals(
                appointmentId,
                result.getAppointmentId()
        );

        assertEquals(
                1500.0,
                result.getAmount()
        );

        assertEquals(
                "UPI",
                result.getPaymentMethod()
        );

        assertEquals(
                PaymentStatus.PENDING,
                result.getPaymentStatus()
        );

        verify(billingRepository)
                .save(any(Bill.class));

        verify(billingProducer)
                .publishBillCreated(any(BillingEvent.class));
    }

    @Test
    void createBill_shouldSetPaymentStatusToPending() {

        when(billingRepository.save(any(Bill.class)))
                .thenAnswer(invocation -> {

                    Bill saved =
                            invocation.getArgument(0);

                    saved.setBillId(billId);

                    return saved;
                });

        billingService.createBill(requestDTO);

        ArgumentCaptor<Bill> captor =
                ArgumentCaptor.forClass(Bill.class);

        verify(billingRepository)
                .save(captor.capture());

        Bill savedBill =
                captor.getValue();

        assertEquals(
                PaymentStatus.PENDING,
                savedBill.getPaymentStatus()
        );
    }

    @Test
    void createBill_shouldPublishCorrectEvent() {

        when(billingRepository.save(any(Bill.class)))
                .thenReturn(bill);

        billingService.createBill(requestDTO);

        ArgumentCaptor<BillingEvent> captor =
                ArgumentCaptor.forClass(BillingEvent.class);

        verify(billingProducer)
                .publishBillCreated(captor.capture());

        BillingEvent event =
                captor.getValue();

        assertEquals(
                billId.toString(),
                event.getBillId()
        );

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId()
        );

        assertEquals(
                patientId.toString(),
                event.getPatientId()
        );

        assertEquals(
                1500.0,
                event.getAmount()
        );

        assertEquals(
                PaymentStatus.PENDING.name(),
                event.getPaymentStatus()
        );

        assertEquals(
                "UPI",
                event.getPaymentMethod()
        );

        assertEquals(
                "BILL_CREATED",
                event.getEventType()
        );

        assertNotNull(event.getOccurredAt());
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void getBillById_shouldReturnBill() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.of(bill));

        BillingResponseDTO result =
                billingService.getBillById(billId);

        assertNotNull(result);

        assertEquals(
                billId,
                result.getBillId()
        );

        assertEquals(
                1500.0,
                result.getAmount()
        );

        verify(billingRepository)
                .findById(billId);
    }

    @Test
    void getBillById_shouldThrowWhenNotFound() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> billingService.getBillById(billId)
                );

        assertEquals(
                "Bill not found with id: " + billId,
                exception.getMessage()
        );

        verify(billingRepository)
                .findById(billId);
    }

    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void getAllBills_shouldReturnAllBills() {

        Bill secondBill =
                Bill.builder()
                        .billId(UUID.randomUUID())
                        .patientId(patientId)
                        .appointmentId(appointmentId)
                        .amount(2500.0)
                        .paymentMethod("CARD")
                        .paymentStatus(PaymentStatus.PAID)
                        .createdAt(LocalDateTime.now())
                        .build();

        when(billingRepository.findAll())
                .thenReturn(
                        List.of(bill, secondBill)
                );

        List<BillingResponseDTO> result =
                billingService.getAllBills();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(
                1500.0,
                result.get(0).getAmount()
        );

        assertEquals(
                2500.0,
                result.get(1).getAmount()
        );

        verify(billingRepository)
                .findAll();
    }

    @Test
    void getAllBills_shouldReturnEmptyList() {

        when(billingRepository.findAll())
                .thenReturn(List.of());

        List<BillingResponseDTO> result =
                billingService.getAllBills();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(billingRepository)
                .findAll();
    }

    // =========================================================
    // GET BY PATIENT
    // =========================================================

    @Test
    void getBillsByPatientId_shouldReturnPatientBills() {

        when(billingRepository.findByPatientId(patientId))
                .thenReturn(List.of(bill));

        List<BillingResponseDTO> result =
                billingService.getBillsByPatientId(patientId);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                billId,
                result.get(0).getBillId()
        );

        assertEquals(
                patientId,
                result.get(0).getPatientId()
        );

        verify(billingRepository)
                .findByPatientId(patientId);
    }

    @Test
    void getBillsByPatientId_shouldReturnEmptyList() {

        when(billingRepository.findByPatientId(patientId))
                .thenReturn(List.of());

        List<BillingResponseDTO> result =
                billingService.getBillsByPatientId(patientId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(billingRepository)
                .findByPatientId(patientId);
    }

    // =========================================================
    // GET BY STATUS
    // =========================================================

    @Test
    void getBillsByStatus_shouldReturnBillsWithStatus() {

        when(billingRepository.findByPaymentStatus(
                PaymentStatus.PAID))
                .thenReturn(List.of(bill));

        bill.setPaymentStatus(PaymentStatus.PAID);

        List<BillingResponseDTO> result =
                billingService.getBillsByStatus(
                        PaymentStatus.PAID
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                PaymentStatus.PAID,
                result.get(0).getPaymentStatus()
        );

        verify(billingRepository)
                .findByPaymentStatus(PaymentStatus.PAID);
    }

    @Test
    void getBillsByStatus_shouldReturnEmptyList() {

        when(billingRepository.findByPaymentStatus(
                PaymentStatus.REFUNDED))
                .thenReturn(List.of());

        List<BillingResponseDTO> result =
                billingService.getBillsByStatus(
                        PaymentStatus.REFUNDED
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(billingRepository)
                .findByPaymentStatus(PaymentStatus.REFUNDED);
    }

    // =========================================================
    // UPDATE PAYMENT STATUS
    // =========================================================

    @Test
    void updatePaymentStatus_shouldUpdateAndSaveBill() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.of(bill));

        when(billingRepository.save(any(Bill.class)))
                .thenReturn(bill);

        BillingResponseDTO result =
                billingService.updatePaymentStatus(
                        billId,
                        PaymentStatus.PAID
                );

        assertNotNull(result);

        assertEquals(
                PaymentStatus.PAID,
                bill.getPaymentStatus()
        );

        verify(billingRepository)
                .findById(billId);

        verify(billingRepository)
                .save(bill);

        verify(billingProducer)
                .publishBillPaid(any(BillingEvent.class));
    }

    @Test
    void updatePaymentStatus_shouldPublishCorrectEvent() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.of(bill));

        when(billingRepository.save(any(Bill.class)))
                .thenReturn(bill);

        billingService.updatePaymentStatus(
                billId,
                PaymentStatus.PAID
        );

        ArgumentCaptor<BillingEvent> captor =
                ArgumentCaptor.forClass(BillingEvent.class);

        verify(billingProducer)
                .publishBillPaid(captor.capture());

        BillingEvent event =
                captor.getValue();

        assertEquals(
                billId.toString(),
                event.getBillId()
        );

        assertEquals(
                patientId.toString(),
                event.getPatientId()
        );

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId()
        );

        assertEquals(
                1500.0,
                event.getAmount()
        );

        assertEquals(
                "UPI",
                event.getPaymentMethod()
        );

        assertEquals(
                PaymentStatus.PAID.name(),
                event.getPaymentStatus()
        );

        assertEquals(
                "BILL_PAID",
                event.getEventType()
        );

        assertNotNull(event.getOccurredAt());
    }

    @Test
    void updatePaymentStatus_shouldThrowWhenBillNotFound() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> billingService.updatePaymentStatus(
                                billId,
                                PaymentStatus.PAID
                        )
                );

        assertEquals(
                "Bill not found with id: " + billId,
                exception.getMessage()
        );

        verify(billingRepository)
                .findById(billId);

        verify(billingRepository, never())
                .save(any(Bill.class));

        verifyNoInteractions(billingProducer);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void deleteBill_shouldDeleteBillAndPublishEvent() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.of(bill));

        billingService.deleteBill(billId);

        verify(billingRepository, times(1))
                .delete(bill);

        verify(billingProducer)
                .publishBillDeleted(any(BillingEvent.class));
    }

    @Test
    void deleteBill_shouldPublishCorrectEvent() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.of(bill));

        billingService.deleteBill(billId);

        ArgumentCaptor<BillingEvent> captor =
                ArgumentCaptor.forClass(BillingEvent.class);

        verify(billingProducer)
                .publishBillDeleted(captor.capture());

        BillingEvent event =
                captor.getValue();

        assertEquals(
                billId.toString(),
                event.getBillId()
        );

        assertEquals(
                patientId.toString(),
                event.getPatientId()
        );

        assertEquals(
                appointmentId.toString(),
                event.getAppointmentId()
        );

        assertEquals(
                "BILL_DELETED",
                event.getEventType()
        );

        assertNotNull(event.getOccurredAt());
    }

    @Test
    void deleteBill_shouldThrowWhenNotFound() {

        when(billingRepository.findById(billId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> billingService.deleteBill(billId)
                );

        assertEquals(
                "Bill not found with id: " + billId,
                exception.getMessage()
        );

        verify(billingRepository)
                .findById(billId);

        verify(billingRepository, never())
                .delete(any(Bill.class));

        verifyNoInteractions(billingProducer);
    }
}