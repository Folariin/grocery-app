package com.mygroceries.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ListItemDtos {

    public record AddListItemRequest(
            @NotBlank @Size(min = 1, max = 120) String itemName,
            BigDecimal quantity,
            @Size(max = 24) String unit,
            @Size(max = 300) String notes
    ) {}

    public record PurchaseRequest(
            @NotNull Boolean purchased
    ) {}

    public record ListItemResponse(
            UUID id,
            UUID listId,
            UUID itemId,
            String itemName,
            BigDecimal quantity,
            String unit,
            String status,
            String notes,
            UUID addedBy,
            UUID purchasedBy,
            LocalDateTime purchasedAt,
            LocalDateTime createdAt
    ) {}
}
