package com.pm.nurseservice.mapper;


import com.pm.nurseservice.dto.NurseRequestDTO;
import com.pm.nurseservice.dto.NurseResponseDTO;
import com.pm.nurseservice.model.Nurse;

public class NurseMapper {

    public static Nurse toEntity(
            NurseRequestDTO dto) {

        return Nurse.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .department(dto.getDepartment())
                .shift(dto.getShift())
                .build();
    }

    public static NurseResponseDTO toDTO(
            Nurse nurse) {

        return NurseResponseDTO.builder()
                .nurseId(nurse.getNurseId())
                .firstName(nurse.getFirstName())
                .lastName(nurse.getLastName())
                .email(nurse.getEmail())
                .phone(nurse.getPhone())
                .department(nurse.getDepartment())
                .shift(nurse.getShift())
                .status(nurse.getStatus())
                .build();
    }
}