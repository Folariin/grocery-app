package com.mygroceries.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class GroceryListDtos {

    public record CreateListRequest(
            @NotBlank @Size(min = 2, max = 80) String name
    ) {}

    public record GroceryListResponse(
            UUID id,
            String name,
            UUID householdId
    ) {}
}
