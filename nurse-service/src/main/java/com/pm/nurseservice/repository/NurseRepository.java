package com.pm.nurseservice.repository;

import com.pm.nurseservice.model.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NurseRepository
        extends JpaRepository<Nurse, UUID> {

    List<Nurse> findByDepartment(
            String department);
}