package com.pm.nurseservice.service;


import com.pm.nurseservice.dto.NurseRequestDTO;
import com.pm.nurseservice.dto.NurseResponseDTO;
import com.pm.nurseservice.enums.NurseStatus;
import com.pm.nurseservice.exception.NurseNotFoundException;
import com.pm.nurseservice.mapper.NurseMapper;
import com.pm.nurseservice.model.Nurse;
import com.pm.nurseservice.repository.NurseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NurseService {

    private final NurseRepository nurseRepository;

    public NurseResponseDTO createNurse(
            NurseRequestDTO dto) {

        Nurse nurse =
                NurseMapper.toEntity(dto);

        nurse.setStatus(
                NurseStatus.ACTIVE);

        return NurseMapper.toDTO(
                nurseRepository.save(nurse)
        );
    }

    public List<NurseResponseDTO> getNurses() {

        return nurseRepository.findAll()
                .stream()
                .map(NurseMapper::toDTO)
                .toList();
    }

    public NurseResponseDTO getNurse(
            UUID nurseId) {

        Nurse nurse =
                nurseRepository.findById(nurseId)
                        .orElseThrow(() ->
                                new NurseNotFoundException(
                                        nurseId
                                ));

        return NurseMapper.toDTO(nurse);
    }
}