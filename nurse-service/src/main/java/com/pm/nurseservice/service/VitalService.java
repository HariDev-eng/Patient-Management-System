package com.pm.nurseservice.service;

import com.pm.nurseservice.dto.VitalRequestDTO;
import com.pm.nurseservice.dto.VitalResponseDTO;
import com.pm.nurseservice.mapper.VitalMapper;
import com.pm.nurseservice.model.VitalRecord;
import com.pm.nurseservice.repository.VitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VitalService {

    private final VitalRepository vitalRepository;

    public VitalResponseDTO createVital(
            VitalRequestDTO dto) {

        VitalRecord saved =
                vitalRepository.save(
                        VitalMapper.toEntity(dto)
                );

        return VitalMapper.toDTO(saved);
    }

    public VitalResponseDTO getVitalById(
            UUID id) {

        VitalRecord vital =
                vitalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vital not found"
                                ));

        return VitalMapper.toDTO(vital);
    }

    public List<VitalResponseDTO>
    getVitalsByPatient(UUID patientId) {

        return vitalRepository
                .findByPatientId(patientId)
                .stream()
                .map(VitalMapper::toDTO)
                .toList();
    }

    public void deleteVital(UUID id) {

        vitalRepository.deleteById(id);
    }
}