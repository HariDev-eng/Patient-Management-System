package com.pm.nurseservice;

import com.pm.nurseservice.dto.NurseRequestDTO;
import com.pm.nurseservice.dto.NurseResponseDTO;
import com.pm.nurseservice.enums.NurseStatus;
import com.pm.nurseservice.exception.NurseNotFoundException;
import com.pm.nurseservice.model.Nurse;
import com.pm.nurseservice.repository.NurseRepository;
import com.pm.nurseservice.service.NurseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NurseServiceApplicationTests {

    @Mock
    private NurseRepository nurseRepository;

    @InjectMocks
    private NurseService nurseService;

    private UUID nurseId;
    private Nurse nurse;
    private NurseRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        nurseId = UUID.randomUUID();

        requestDTO = new NurseRequestDTO();

        requestDTO.setFirstName("Alice");
        requestDTO.setLastName("Johnson");
        requestDTO.setEmail("alice.johnson@example.com");
        requestDTO.setPhone("+1-555-0200");
        requestDTO.setDepartment("Emergency");
        requestDTO.setShift("Morning");

        nurse = Nurse.builder()
                .nurseId(nurseId)
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .phone("+1-555-0200")
                .department("Emergency")
                .shift("Morning")
                .status(NurseStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // =========================================================
    // CREATE NURSE
    // =========================================================

    @Test
    void shouldCreateNurseSuccessfully() {

        when(nurseRepository.save(any(Nurse.class)))
                .thenReturn(nurse);

        NurseResponseDTO result =
                nurseService.createNurse(requestDTO);

        assertNotNull(result);

        assertEquals(
                nurseId,
                result.getNurseId()
        );

        assertEquals(
                "Alice",
                result.getFirstName()
        );

        assertEquals(
                "Johnson",
                result.getLastName()
        );

        assertEquals(
                "alice.johnson@example.com",
                result.getEmail()
        );

        assertEquals(
                "Emergency",
                result.getDepartment()
        );

        assertEquals(
                "Morning",
                result.getShift()
        );

        assertEquals(
                NurseStatus.ACTIVE,
                result.getStatus()
        );

        verify(nurseRepository)
                .save(any(Nurse.class));
    }

    @Test
    void shouldSetNewNurseStatusToActive() {

        when(nurseRepository.save(any(Nurse.class)))
                .thenReturn(nurse);

        nurseService.createNurse(requestDTO);

        verify(nurseRepository).save(
                argThat(savedNurse ->
                        savedNurse.getStatus()
                                == NurseStatus.ACTIVE
                )
        );
    }

    // =========================================================
    // GET ALL NURSES
    // =========================================================

    @Test
    void shouldReturnAllNurses() {

        when(nurseRepository.findAll())
                .thenReturn(List.of(nurse));

        List<NurseResponseDTO> result =
                nurseService.getNurses();

        assertNotNull(result);

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                nurseId,
                result.get(0).getNurseId()
        );

        assertEquals(
                "Alice",
                result.get(0).getFirstName()
        );

        verify(nurseRepository)
                .findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoNursesExist() {

        when(nurseRepository.findAll())
                .thenReturn(List.of());

        List<NurseResponseDTO> result =
                nurseService.getNurses();

        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(nurseRepository)
                .findAll();
    }

    // =========================================================
    // GET NURSE BY ID
    // =========================================================

    @Test
    void shouldReturnNurseById() {

        when(nurseRepository.findById(nurseId))
                .thenReturn(Optional.of(nurse));

        NurseResponseDTO result =
                nurseService.getNurse(nurseId);

        assertNotNull(result);

        assertEquals(
                nurseId,
                result.getNurseId()
        );

        assertEquals(
                "Alice",
                result.getFirstName()
        );

        assertEquals(
                "alice.johnson@example.com",
                result.getEmail()
        );

        assertEquals(
                NurseStatus.ACTIVE,
                result.getStatus()
        );

        verify(nurseRepository)
                .findById(nurseId);
    }

    @Test
    void shouldThrowExceptionWhenNurseDoesNotExist() {

        when(nurseRepository.findById(nurseId))
                .thenReturn(Optional.empty());

        assertThrows(
                NurseNotFoundException.class,
                () -> nurseService.getNurse(nurseId)
        );

        verify(nurseRepository)
                .findById(nurseId);
    }
}