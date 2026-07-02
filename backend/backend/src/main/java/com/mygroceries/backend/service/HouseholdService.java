package com.mygroceries.backend.service;

import com.mygroceries.backend.dto.HouseholdDtos.HouseholdResponse;
import com.mygroceries.backend.model.Household;
import com.mygroceries.backend.model.HouseholdMember;
import com.mygroceries.backend.model.User;
import com.mygroceries.backend.repo.HouseholdMemberRepository;
import com.mygroceries.backend.repo.HouseholdRepository;
import com.mygroceries.backend.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final HouseholdMemberRepository memberRepository;
    private final UserRepository userRepository;

    public HouseholdService(HouseholdRepository householdRepository,
                            HouseholdMemberRepository memberRepository,
                            UserRepository userRepository) {
        this.householdRepository = householdRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    public HouseholdResponse createHousehold(UUID userId, String name) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Household household = new Household();
        household.setName(name.trim());
        household.setCreatedBy(creator);

        Household saved = householdRepository.save(household);

        HouseholdMember ownerMembership = new HouseholdMember();
        ownerMembership.setHousehold(saved);
        ownerMembership.setUser(creator);
        ownerMembership.setRole("OWNER");
        ownerMembership.setStatus("ACTIVE");

        memberRepository.save(ownerMembership);

        return new HouseholdResponse(saved.getId(), saved.getName(), "OWNER");
    }

    @Transactional(readOnly = true)
    public List<HouseholdResponse> listMyHouseholds(UUID userId) {
        List<HouseholdMember> memberships = memberRepository.findByUser_IdAndStatus(userId, "ACTIVE");

        List<HouseholdResponse> result = new ArrayList<>();
        for (HouseholdMember m : memberships) {
            Household h = m.getHousehold();
            result.add(new HouseholdResponse(h.getId(), h.getName(), m.getRole()));
        }


        return result;
    }
}