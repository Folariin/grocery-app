package com.mygroceries.backend.service;

import com.mygroceries.backend.dto.InviteDtos.AcceptInviteResponse;
import com.mygroceries.backend.dto.InviteDtos.InviteResponse;
import com.mygroceries.backend.model.*;
import com.mygroceries.backend.model.Invitation.Status;
import com.mygroceries.backend.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;

    public InvitationService(
            InvitationRepository invitationRepository,
            HouseholdRepository householdRepository,
            HouseholdMemberRepository householdMemberRepository,
            UserRepository userRepository
    ) {
        this.invitationRepository = invitationRepository;
        this.householdRepository = householdRepository;
        this.householdMemberRepository = householdMemberRepository;
        this.userRepository = userRepository;
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private Household getHousehold(UUID householdId) {
        return householdRepository.findById(householdId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Household not found"));
    }

    private HouseholdMember requireActiveMember(UUID householdId, UUID userId) {
        HouseholdMember m = householdMemberRepository
                .findByHousehold_IdAndUser_Id(householdId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a household member"));

        if (!"ACTIVE".equalsIgnoreCase(m.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Membership not active");
        }
        return m;
    }

    private void requireOwner(UUID householdId, UUID userId) {
        HouseholdMember m = requireActiveMember(householdId, userId);
        if (!"OWNER".equalsIgnoreCase(m.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only OWNER can invite");
        }
    }

    private InviteResponse toResponse(Invitation inv) {
        return new InviteResponse(
                inv.getId(),
                inv.getToken(),
                inv.getHousehold().getId(),
                inv.getHousehold().getName(),
                inv.getEmail(),
                inv.getStatus().name(),
                inv.getCreatedAt(),
                inv.getExpiresAt()
        );
    }

    public InviteResponse createInvite(UUID inviterUserId, UUID householdId, String email, Integer expiresInDays) {
        requireOwner(householdId, inviterUserId);

        User inviter = getUser(inviterUserId);
        Household household = getHousehold(householdId);

        Invitation inv = new Invitation();
        inv.setToken(UUID.randomUUID().toString());
        inv.setHousehold(household);
        inv.setInvitedBy(inviter);
        inv.setEmail(email.trim().toLowerCase());
        inv.setStatus(Status.PENDING);

        if (expiresInDays != null && expiresInDays > 0) {
            inv.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));
        } else {
            // optional default expiry:
            inv.setExpiresAt(LocalDateTime.now().plusDays(7));
        }

        Invitation saved = invitationRepository.save(inv);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InviteResponse> myPendingInvites(UUID userId) {
        User me = getUser(userId);
        return invitationRepository
                .findByEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(me.getEmail(), Status.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AcceptInviteResponse accept(UUID userId, String token) {
        User me = getUser(userId);

        Invitation inv = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invite not found"));

        // Expiry check
        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(LocalDateTime.now())) {
            inv.setStatus(Status.EXPIRED);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite expired");
        }

        if (inv.getStatus() != Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invite not pending");
        }

        // Email must match logged-in user's email
        if (!inv.getEmail().equalsIgnoreCase(me.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invite is not for your email");
        }

        UUID householdId = inv.getHousehold().getId();

        // If already a member, just mark accepted and return
        householdMemberRepository.findByHousehold_IdAndUser_Id(householdId, me.getId())
                .ifPresent(existing -> {
                    inv.setStatus(Status.ACCEPTED);
                    inv.setAcceptedBy(me);
                    inv.setAcceptedAt(LocalDateTime.now());
                });

        // Otherwise, create membership
        if (householdMemberRepository.findByHousehold_IdAndUser_Id(householdId, me.getId()).isEmpty()) {
            HouseholdMember m = new HouseholdMember();
            m.setHousehold(inv.getHousehold());
            m.setUser(me);
            m.setRole("MEMBER");
            m.setStatus("ACTIVE");
            // joinedAt if you have it

            householdMemberRepository.save(m);

            inv.setStatus(Status.ACCEPTED);
            inv.setAcceptedBy(me);
            inv.setAcceptedAt(LocalDateTime.now());
        }

        return new AcceptInviteResponse(householdId, inv.getHousehold().getName(), "MEMBER");
    }
}

