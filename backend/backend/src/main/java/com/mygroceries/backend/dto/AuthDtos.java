package com.mygroceries.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record SignupRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 2, max = 60) String displayName,
            @NotBlank @Size(min = 6, max = 100) String password
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record ForgotPasswordRequest(
            @Email @NotBlank String email
    ) {}

    public record ResetPasswordRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 6, max = 100) String password,
            @NotBlank @Size(min = 6, max = 100) String confirmPassword
    ) {}

    public record AuthResponse(String token) {}

    public record MessageResponse(String message) {}
}
