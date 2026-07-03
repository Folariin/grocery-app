package com.mygroceries.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class HouseholdDtos {

    public record CreateHouseholdRequest(
            @NotBlank @Size(min = 2, max = 80) String name
    ) {}

    public record UpdateHouseholdRequest(
            @NotBlank @Size(min = 2, max = 80) String name
    ) {}

    public record HouseholdResponse(
            UUID id,
            String name,
            String role
    ) {}

    public record HouseholdActionResponse(
            String message
    ) {}

    public record HouseholdMemberResponse(
            UUID id,
            UUID userId,
            String displayName,
            String email,
            String role,
            String status,
            LocalDateTime joinedAt
    ) {}
}
