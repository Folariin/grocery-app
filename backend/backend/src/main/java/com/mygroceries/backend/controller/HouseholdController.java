package com.mygroceries.backend.controller;

import com.mygroceries.backend.dto.HouseholdDtos.CreateHouseholdRequest;
import com.mygroceries.backend.dto.HouseholdDtos.HouseholdActionResponse;
import com.mygroceries.backend.dto.HouseholdDtos.HouseholdMemberResponse;
import com.mygroceries.backend.dto.HouseholdDtos.HouseholdResponse;
import com.mygroceries.backend.dto.HouseholdDtos.UpdateHouseholdRequest;
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

    // GET /api/households/{householdId}
    @GetMapping("/{householdId}")
    public ResponseEntity<HouseholdResponse> household(
            Authentication auth,
            @PathVariable UUID householdId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(householdService.getHousehold(userId, householdId));
    }

    // PATCH /api/households/{householdId}
    @PatchMapping("/{householdId}")
    public ResponseEntity<HouseholdResponse> renameHousehold(
            Authentication auth,
            @PathVariable UUID householdId,
            @Valid @RequestBody UpdateHouseholdRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(householdService.renameHousehold(userId, householdId, req.name()));
    }

    // POST /api/households/{householdId}/leave
    @PostMapping("/{householdId}/leave")
    public ResponseEntity<HouseholdActionResponse> leaveHousehold(
            Authentication auth,
            @PathVariable UUID householdId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(householdService.leaveHousehold(userId, householdId));
    }

    // DELETE /api/households/{householdId}
    @DeleteMapping("/{householdId}")
    public ResponseEntity<HouseholdActionResponse> closeHousehold(
            Authentication auth,
            @PathVariable UUID householdId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(householdService.closeHousehold(userId, householdId));
    }

    // GET /api/households/{householdId}/members
    @GetMapping("/{householdId}/members")
    public ResponseEntity<List<HouseholdMemberResponse>> householdMembers(
            Authentication auth,
            @PathVariable UUID householdId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(householdService.listMembers(userId, householdId));
    }
}
