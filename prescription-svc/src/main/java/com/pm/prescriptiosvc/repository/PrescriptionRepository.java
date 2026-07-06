package src.main.java.com.pm.prescriptiosvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.main.java.com.pm.prescriptiosvc.model.Prescription;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository
        extends JpaRepository<Prescription, UUID> {

    List<Prescription> findByPatientId(
            UUID patientId);
}