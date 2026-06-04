package com.pm.doctorsvc.repository;

import com.pm.doctorsvc.enums.Specialization;
import com.pm.doctorsvc.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository
        extends JpaRepository<Doctor, UUID> {

    boolean existsByEmail(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByEmailAndDoctorIdNot(
            String email,
            UUID doctorId
    );

    boolean existsByLicenseNumberAndDoctorIdNot(
            String licenseNumber,
            UUID doctorId
    );

    List<Doctor> findBySpecialization(
            Specialization specialization
    );
}