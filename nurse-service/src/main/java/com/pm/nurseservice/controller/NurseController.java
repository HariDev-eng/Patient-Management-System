package com.pm.nurseservice.controller;


import com.pm.nurseservice.dto.NurseRequestDTO;
import com.pm.nurseservice.dto.NurseResponseDTO;
import com.pm.nurseservice.service.NurseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/nurses")
@AllArgsConstructor
public class NurseController {

    private final NurseService nurseService;

    @PostMapping
    public ResponseEntity<NurseResponseDTO>
    createNurse(
            @Valid
            @RequestBody NurseRequestDTO dto) {

        return ResponseEntity.ok(
                nurseService.createNurse(dto)
        );
    }

    @GetMapping
    public ResponseEntity<List<NurseResponseDTO>>
    getNurses() {

        return ResponseEntity.ok(
                nurseService.getNurses()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<NurseResponseDTO>
    getNurse(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                nurseService.getNurse(id)
        );
    }
}