//package com.pm.analyticsservice.Repository;
//
//import com.pm.analyticsservice.entity.AnalyticsBilling;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.UUID;
//
//public interface AnalyticsBillingRepository
//        extends JpaRepository<AnalyticsBilling, UUID> {
//
//    List<AnalyticsBilling> findByPatientId(UUID patientId);
//
//    List<AnalyticsBilling> findByPaymentStatus(String paymentStatus);
//}