package com.mygroceries.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "household_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"household_id", "user_id"})
        }
)
public class HouseholdMember {

    @Id
    @GeneratedValue
    private UUID id;

    // Many members belong to one household
    @ManyToOne(optional = false)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    // Each record refers to one user
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Role (e.g., OWNER, MEMBER, ADMIN)
    @Column(nullable = false)
    private String role = "MEMBER";

    // Membership status (e.g., ACTIVE, PENDING, LEFT)
    @Column(nullable = false)
    private String status = "ACTIVE";

    // Timestamp for when they joined
    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    public HouseholdMember() {}

    public HouseholdMember(Household household, User user, String role) {
        this.household = household;
        this.user = user;
        this.role = role;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Household getHousehold() { return household; }
    public void setHousehold(Household household) { this.household = household; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
