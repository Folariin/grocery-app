package com.mygroceries.backend.controller;

import com.mygroceries.backend.dto.GroceryListDtos.CreateListRequest;
import com.mygroceries.backend.dto.GroceryListDtos.GroceryListResponse;
import com.mygroceries.backend.service.GroceryListService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/households/{householdId}/lists")
public class GroceryListController {

    private final GroceryListService groceryListService;

    public GroceryListController(GroceryListService groceryListService) {
        this.groceryListService = groceryListService;
    }

    // POST /api/households/{householdId}/lists
    @PostMapping
    public ResponseEntity<GroceryListResponse> createList(
            Authentication auth,
            @PathVariable UUID householdId,
            @Valid @RequestBody CreateListRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        GroceryListResponse created = groceryListService.createList(userId, householdId, req.name());
        return ResponseEntity.status(201).body(created);
    }

    // GET /api/households/{householdId}/lists
    @GetMapping
    public ResponseEntity<List<GroceryListResponse>> listLists(
            Authentication auth,
            @PathVariable UUID householdId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(groceryListService.listLists(userId, householdId));
    }
}

