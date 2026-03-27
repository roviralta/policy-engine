package com.rovi.policy_engine.service;

import com.rovi.policy_engine.dto.request.LoginRequest;
import com.rovi.policy_engine.dto.request.RegisterRequest;
import com.rovi.policy_engine.dto.response.TokenResponse;
import com.rovi.policy_engine.repository.UserRepository;
import com.rovi.policy_engine.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.exists(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userRepository.save(request.getUsername(), encodedPassword);

        String token = jwtService.generateToken(request.getUsername());

        log.info("User registered: {}", request.getUsername());

        return TokenResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(request.getUsername());

        log.info("User logged in: {}", request.getUsername());

        return TokenResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }
}
