package com.mygroceries.backend.repo;

import com.mygroceries.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
