package com.mygroceries.backend.repo;

import com.mygroceries.backend.model.ListItem;
import com.mygroceries.backend.model.ListItem.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ListItemRepository extends JpaRepository<ListItem, UUID> {
    List<ListItem> findByList_Id(UUID listId);
    List<ListItem> findByList_IdAndStatusNot(UUID listId, Status status); // e.g., hide REMOVED

    // Items assigned to a user and still open
    @Query("""
      select li from ListItem li
      where li.assignedTo.id = :userId and li.status in ('NEEDED','IN_CART')
      order by li.createdAt desc
    """)
    List<ListItem> findOpenAssignedTo(UUID userId);
}
