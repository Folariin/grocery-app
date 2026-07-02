package com.mygroceries.backend.repo;

import com.mygroceries.backend.model.GroceryList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface GroceryListRepository extends JpaRepository<GroceryList, UUID> {
    List<GroceryList> findByHousehold_IdOrderByCreatedAtDesc(UUID householdId);
}
