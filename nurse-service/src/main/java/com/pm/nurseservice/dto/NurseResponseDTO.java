package com.pm.nurseservice.dto;


import com.pm.nurseservice.enums.NurseStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NurseResponseDTO {

    private UUID nurseId;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String department;

    private String shift;

    private NurseStatus status;
}