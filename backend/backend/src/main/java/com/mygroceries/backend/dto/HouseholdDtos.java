package com.mygroceries.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class HouseholdDtos {

    public record CreateHouseholdRequest(
            @NotBlank @Size(min = 2, max = 80) String name
    ) {}

    public record HouseholdResponse(
            UUID id,
            String name,
            String role
    ) {}
}
