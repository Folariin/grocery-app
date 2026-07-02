package com.mygroceries.backend.controller;

import com.mygroceries.backend.dto.InviteDtos.*;
import com.mygroceries.backend.service.InvitationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    // OWNER creates invite
    @PostMapping("/households/{householdId}/invites")
    public ResponseEntity<InviteResponse> createInvite(
            Authentication auth,
            @PathVariable UUID householdId,
            @Valid @RequestBody CreateInviteRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        InviteResponse created = invitationService.createInvite(userId, householdId, req.email(), req.expiresInDays());
        return ResponseEntity.status(201).body(created);
    }

    // Logged-in user sees their pending invites
    @GetMapping("/invites")
    public ResponseEntity<List<InviteResponse>> myInvites(Authentication auth) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(invitationService.myPendingInvites(userId));
    }

    // Accept by token
    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<AcceptInviteResponse> accept(
            Authentication auth,
            @PathVariable String token
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(invitationService.accept(userId, token));
    }
}
