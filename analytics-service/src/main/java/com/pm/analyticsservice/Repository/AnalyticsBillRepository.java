package com.pm.analyticsservice.Repository;

import com.pm.analyticsservice.model.AnalyticsBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsBillRepository
        extends JpaRepository<AnalyticsBill, UUID> {

}