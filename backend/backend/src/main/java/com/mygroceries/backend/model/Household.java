package com.mygroceries.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "households")
public class Household {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    // Who created the household (not the only member)
    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HouseholdMember> members = new HashSet<>();

    // Optional: navigate to lists (1:N)
    @OneToMany(mappedBy = "household", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroceryList> lists = new ArrayList<>();

    public Household() {}
    public Household(String name, User createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    // Getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<HouseholdMember> getMembers() { return members; }
    public void setMembers(Set<HouseholdMember> members) { this.members = members; }

    public List<GroceryList> getLists() { return lists; }
    public void setLists(List<GroceryList> lists) { this.lists = lists; }
}