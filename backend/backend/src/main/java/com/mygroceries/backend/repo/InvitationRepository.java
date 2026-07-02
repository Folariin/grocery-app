package com.mygroceries.backend.repo;

import com.mygroceries.backend.model.Invitation;
import com.mygroceries.backend.model.Invitation.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Optional<Invitation> findByToken(String token);

    List<Invitation> findByEmailIgnoreCaseAndStatusOrderByCreatedAtDesc(String email, Status status);
}
