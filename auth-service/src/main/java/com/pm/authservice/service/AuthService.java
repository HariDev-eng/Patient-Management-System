package com.pm.authservice.service;

import com.pm.authservice.config.SecurityConfig;
import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.SignupRequestDTO;
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

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<String> token = userService
                .findByEmail(loginRequestDTO.getEmail())
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(),u.getPassword()))
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));

        return token;
    }

    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }

    public Optional<String> signup(SignupRequestDTO signupRequestDTO) {

        if(userService.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
            return Optional.empty();
        }

        User user = new User();

        user.setEmail(signupRequestDTO.getEmail());

        user.setPassword(
                passwordEncoder.encode(signupRequestDTO.getPassword())
        );

        user.setRole("USER");

        User savedUser = userService.save(user);

        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getRole()
        );

        return Optional.of(token);
    }
}
