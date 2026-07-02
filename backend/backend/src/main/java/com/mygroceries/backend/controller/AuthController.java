package com.mygroceries.backend.controller;

import com.mygroceries.backend.security.JwtService;
import com.mygroceries.backend.model.User;
import com.mygroceries.backend.security.JwtUtil;
import com.mygroceries.backend.service.UserService;
import com.mygroceries.backend.dto.AuthDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest req) {

        User u = userService.signup(req.email(), req.displayName(), req.password());

        String token = jwtService.generate(
                u.getId().toString(),
                Map.of("email", u.getEmail(), "name", u.getDisplayName())
        );

        return ResponseEntity.status(201).body(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        User u = userService.findByEmail(req.email())
                .orElseThrow(UserService.InvalidCredentialsException::new);

        if (!userService.passwordMatches(req.password(), u.getPasswordHash())) {
            throw new UserService.InvalidCredentialsException();
        }

        String token = jwtService.generate(
                u.getId().toString(),
                Map.of("email", u.getEmail(), "name", u.getDisplayName())
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }




}