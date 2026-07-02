package com.mygroceries.backend.repo;

import com.mygroceries.backend.model.HouseholdMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface HouseholdMemberRepository extends JpaRepository<HouseholdMember, UUID> {
    Optional<HouseholdMember> findByHousehold_IdAndUser_Id(UUID householdId, UUID userId);
    boolean existsByHousehold_IdAndUser_Id(UUID householdId, UUID userId);
    long countByHousehold_Id(UUID householdId);
    List<HouseholdMember> findByUser_IdAndStatus(UUID userId, String status);
}
