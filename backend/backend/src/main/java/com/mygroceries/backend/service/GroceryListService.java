package com.mygroceries.backend.service;

import com.mygroceries.backend.dto.GroceryListDtos.GroceryListResponse;
import com.mygroceries.backend.model.GroceryList;
import com.mygroceries.backend.model.Household;
import com.mygroceries.backend.model.HouseholdMember;
import com.mygroceries.backend.model.User;
import com.mygroceries.backend.repo.GroceryListRepository;
import com.mygroceries.backend.repo.HouseholdMemberRepository;
import com.mygroceries.backend.repo.HouseholdRepository;
import com.mygroceries.backend.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GroceryListService {

    private final GroceryListRepository groceryListRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final UserRepository userRepository;

    public GroceryListService(GroceryListRepository groceryListRepository,
                              HouseholdRepository householdRepository,
                              HouseholdMemberRepository householdMemberRepository,
                              UserRepository userRepository) {
        this.groceryListRepository = groceryListRepository;
        this.householdRepository = householdRepository;
        this.householdMemberRepository = householdMemberRepository;
        this.userRepository = userRepository;
    }

    private void assertActiveMember(UUID householdId, UUID userId) {
        HouseholdMember m = householdMemberRepository
                .findByHousehold_IdAndUser_Id(householdId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a household member"));

        // If you use status like "ACTIVE"
        if (!"ACTIVE".equalsIgnoreCase(m.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Membership not active");
        }
    }

    public GroceryListResponse createList(UUID userId, UUID householdId, String name) {
        assertActiveMember(householdId, userId);

        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Household not found"));

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GroceryList list = new GroceryList();
        list.setName(name.trim());
        list.setHousehold(household);

        // If your GroceryList entity has createdBy, keep this.
        // If not, delete the next line.
        list.setCreatedBy(creator);

        GroceryList saved = groceryListRepository.save(list);
        return new GroceryListResponse(saved.getId(), saved.getName(), householdId);
    }

    @Transactional(readOnly = true)
    public List<GroceryListResponse> listLists(UUID userId, UUID householdId) {
        assertActiveMember(householdId, userId);

        return groceryListRepository
                .findByHousehold_IdOrderByCreatedAtDesc(householdId)
                .stream()
                .map(gl -> new GroceryListResponse(gl.getId(), gl.getName(), householdId))
                .toList();
    }

}
