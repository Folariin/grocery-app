package com.mygroceries.backend.repo;

import com.mygroceries.backend.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface HouseholdRepository extends JpaRepository<Household, UUID> {
    // All households a given user belongs to (via HouseholdMember)
    @Query("""
      select h from Household h
      join h.members m
      where m.user.id = :userId and m.status = 'ACTIVE'
      order by h.createdAt desc
      """)
    List<Household> findAllForUser(UUID userId);
}
