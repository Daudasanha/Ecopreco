package com.ecopreco.controller;

import com.ecopreco.dto.AuthRequest;
import com.ecopreco.dto.AuthResponse;
import com.ecopreco.dto.MessageResponse;
import com.ecopreco.dto.RegisterRequest;
import com.ecopreco.exception.BadRequestException;
import com.ecopreco.model.User;
import com.ecopreco.security.JwtTokenProvider;
import com.ecopreco.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userService.getUserByUsername(authRequest.getUsername());

        Set<String> roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                roles // <-- Roles incluídas corretamente na resposta
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username já está em uso!");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email já está em uso!");
        }

        userService.createUser(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Usuário registrado com sucesso!"));
    }
}