package com.mygroceries.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class UserDtos {

    public record UserProfileResponse(
            UUID id,
            String displayName,
            String email
    ) {}

    public record UpdateProfileRequest(
            @NotBlank @Size(min = 2, max = 60) String displayName
    ) {}
}
