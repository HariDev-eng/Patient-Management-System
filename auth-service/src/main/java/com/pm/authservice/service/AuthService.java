package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.dto.SignupRequestDTO;
import com.pm.authservice.enums.Role;
import com.pm.authservice.model.User;
import com.pm.authservice.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<LoginResponseDTO> authenticate(
            LoginRequestDTO dto) {

        return userService.findByEmail(dto.getEmail())
                .filter(user ->
                        passwordEncoder.matches(
                                dto.getPassword(),
                                user.getPassword()))
                .map(user -> {

                    String token =
                            jwtUtil.generateToken(
                                    user.getEmail(),
                                    user.getRole().name());

                    return new LoginResponseDTO(
                            token,
                            user.getEmail(),
                            user.getRole().name());
                });
    }

    public Optional<LoginResponseDTO> signup(
            SignupRequestDTO dto) {

        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            return Optional.empty();
        }

        User user = new User();

        user.setEmail(dto.getEmail());

        user.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        user.setRole(Role.PATIENT);

        User savedUser =
                userService.save(user);

        String token =
                jwtUtil.generateToken(
                        savedUser.getEmail(),
                        savedUser.getRole().name());

        return Optional.of(
                new LoginResponseDTO(
                        token,
                        savedUser.getEmail(),
                        savedUser.getRole().name()
                )
        );
    }

    public boolean validateToken(String token) {

        try {
            jwtUtil.validateToken(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }
}