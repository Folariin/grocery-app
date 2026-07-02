package com.mygroceries.backend.service;

import com.mygroceries.backend.dto.ListItemDtos.ListItemResponse;
import com.mygroceries.backend.model.*;
import com.mygroceries.backend.model.ListItem.Status;
import com.mygroceries.backend.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ListItemService {

    private final ListItemRepository listItemRepository;
    private final GroceryListRepository groceryListRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ListItemService(
            ListItemRepository listItemRepository,
            GroceryListRepository groceryListRepository,
            HouseholdMemberRepository householdMemberRepository,
            UserRepository userRepository,
            ItemRepository itemRepository
    ) {
        this.listItemRepository = listItemRepository;
        this.groceryListRepository = groceryListRepository;
        this.householdMemberRepository = householdMemberRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    private GroceryList getListAndAssertMember(UUID listId, UUID userId) {
        GroceryList list = groceryListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List not found"));

        UUID householdId = list.getHousehold().getId();

        HouseholdMember m = householdMemberRepository
                .findByHousehold_IdAndUser_Id(householdId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a household member"));

        if (!"ACTIVE".equalsIgnoreCase(m.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Membership not active");
        }

        return list;
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private ListItemResponse toResponse(ListItem li) {
        return new ListItemResponse(
                li.getId(),
                li.getList().getId(),
                li.getItem().getId(),
                li.getItem().getName(),              // assumes Item has getName()
                li.getQuantity(),
                li.getUnit(),
                li.getStatus().name(),
                li.getNotes(),
                li.getAddedBy().getId(),
                li.getPurchasedBy() == null ? null : li.getPurchasedBy().getId(),
                li.getPurchasedAt(),
                li.getCreatedAt()
        );
    }

    public ListItemResponse addItem(UUID userId, UUID listId, String itemName, BigDecimal quantity, String unit, String notes) {
        GroceryList list = getListAndAssertMember(listId, userId);
        User user = getUser(userId);

        String cleanName = itemName.trim();

        Item item = itemRepository.findByNameIgnoreCase(cleanName)
                .orElseGet(() -> {
                    Item created = new Item();
                    created.setName(cleanName);
                    return itemRepository.save(created);
                });

        ListItem li = new ListItem();
        li.setList(list);
        li.setItem(item);
        li.setAddedBy(user);

        li.setQuantity(quantity == null ? BigDecimal.ONE : quantity);
        li.setUnit(unit == null ? null : unit.trim());
        li.setNotes(notes == null ? null : notes.trim());

        li.setStatus(Status.NEEDED);
        li.setCreatedAt(LocalDateTime.now());

        ListItem saved = listItemRepository.save(li);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ListItemResponse> getItems(UUID userId, UUID listId) {
        getListAndAssertMember(listId, userId);

        return listItemRepository.findByList_IdAndStatusNot(listId, Status.REMOVED).stream()
                .sorted(java.util.Comparator.comparing(ListItem::getCreatedAt).reversed())
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }


    public ListItemResponse setPurchased(UUID userId, UUID listItemId, boolean purchased) {
        ListItem li = listItemRepository.findById(listItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List item not found"));

        UUID listId = li.getList().getId();
        getListAndAssertMember(listId, userId);

        User user = getUser(userId);

        if (purchased) {
            li.setStatus(Status.PURCHASED);
            li.setPurchasedBy(user);
            li.setPurchasedAt(LocalDateTime.now());
        } else {
            li.setStatus(Status.NEEDED);
            li.setPurchasedBy(null);
            li.setPurchasedAt(null);
        }

        return toResponse(li);
    }

    public void removeItem(UUID userId, UUID listItemId) {
        ListItem li = listItemRepository.findById(listItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "List item not found"));

        UUID listId = li.getList().getId();
        getListAndAssertMember(listId, userId);

        li.setStatus(Status.REMOVED);
        // we keep it in DB (soft delete)
    }
}
