package com.pm.billingservice.repository;

import com.pm.billingservice.enums.PaymentStatus;
import com.pm.billingservice.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillingRepository
        extends JpaRepository<Bill, UUID> {

    List<Bill> findByPatientId(UUID patientId);

    List<Bill> findByPaymentStatus(
            PaymentStatus paymentStatus);
}