package com.mygroceries.backend.controller;

import com.mygroceries.backend.dto.HouseholdDtos.CreateHouseholdRequest;
import com.mygroceries.backend.dto.HouseholdDtos.HouseholdResponse;
import com.mygroceries.backend.service.HouseholdService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    // POST /api/households
    @PostMapping
    public ResponseEntity<HouseholdResponse> createHousehold(
            Authentication auth,
            @Valid @RequestBody CreateHouseholdRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        HouseholdResponse created = householdService.createHousehold(userId, req.name());
        return ResponseEntity.status(201).body(created);
    }

    // GET /api/households
    @GetMapping
    public ResponseEntity<List<HouseholdResponse>> myHouseholds(Authentication auth) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(householdService.listMyHouseholds(userId));
    }
}
