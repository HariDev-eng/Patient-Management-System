package com.pm.authservice.controller;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.dto.SignupRequestDTO;
import com.pm.authservice.model.User;
import com.pm.authservice.service.AuthService;
import com.pm.authservice.service.UserService;
import com.pm.authservice.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthService authService,
            UserService userService,
            JwtUtil jwtUtil) {

        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        Optional<LoginResponseDTO> response =
                authService.authenticate(dto);

        return response
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .build());
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDTO> signup(
            @Valid @RequestBody SignupRequestDTO dto) {

        Optional<LoginResponseDTO> response =
                authService.signup(dto);

        return response
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .build());
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @RequestHeader("Authorization")
            String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        String token =
                authHeader.substring(7);

        return authService.validateToken(token)
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED)
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @RequestHeader("Authorization")
            String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        String token =
                authHeader.substring(7);

        String email =
                jwtUtil.extractEmail(token);

        User user =
                userService.findByEmail(email)
                        .orElseThrow();

        return ResponseEntity.ok(
                Map.of(
                        "email", user.getEmail(),
                        "role", user.getRole()
                )
        );
    }
}