package com.pm.nurseservice.repository;

import com.pm.nurseservice.model.VitalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VitalRepository
        extends JpaRepository<VitalRecord, UUID> {

    List<VitalRecord> findByPatientId(UUID patientId);
}