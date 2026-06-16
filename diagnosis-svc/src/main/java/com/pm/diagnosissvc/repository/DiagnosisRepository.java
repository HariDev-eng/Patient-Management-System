package com.pm.diagnosissvc.repository;

import com.pm.diagnosissvc.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagnosisRepository
        extends JpaRepository<Diagnosis, UUID> {

    List<Diagnosis> findByPatientId(
            UUID patientId);

    List<Diagnosis> findByDoctorId(
            UUID doctorId);
}