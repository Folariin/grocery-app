package com.mygroceries.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class InviteDtos {

    public record CreateInviteRequest(
            @NotBlank @Email String email,
            // optional: number of days until expiry (or just ignore in service)
            Integer expiresInDays
    ) {}

    public record InviteResponse(
            UUID id,
            String token,
            UUID householdId,
            String householdName,
            String email,
            String status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt
    ) {}

    public record AcceptInviteResponse(
            UUID householdId,
            String householdName,
            String role
    ) {}
}
