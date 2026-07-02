package com.mygroceries.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "list_items",
        indexes = {
                @Index(name = "ix_list_items_list", columnList = "list_id"),
                @Index(name = "ix_list_items_status", columnList = "status")
        }
)
public class ListItem {

    @Id
    @GeneratedValue
    private UUID id;

    // The list this line belongs to
    @ManyToOne(optional = false)
    @JoinColumn(name = "list_id", nullable = false)
    private GroceryList list;

    // The catalog item (Milk, Bread, etc.)
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // Quantity + unit shown on the list
    @Column(nullable = false)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(length = 24)
    private String unit; // e.g., "pcs", "kg", "L" (optional)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status = Status.NEEDED;

    @Column(length = 300)
    private String notes;

    // Who added/purchased
    @ManyToOne(optional = false)
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    @ManyToOne
    @JoinColumn(name = "purchased_by")
    private User purchasedBy;

    private LocalDateTime purchasedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Optional: assign someone to pick it up
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    // Optional: price paid when purchased
    private BigDecimal pricePaid;

    // --- getters/setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public GroceryList getList() { return list; }
    public void setList(GroceryList list) { this.list = list; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getAddedBy() { return addedBy; }
    public void setAddedBy(User addedBy) { this.addedBy = addedBy; }

    public User getPurchasedBy() { return purchasedBy; }
    public void setPurchasedBy(User purchasedBy) { this.purchasedBy = purchasedBy; }

    public LocalDateTime getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(LocalDateTime purchasedAt) { this.purchasedAt = purchasedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getAssignedTo() { return assignedTo; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }

    public BigDecimal getPricePaid() { return pricePaid; }
    public void setPricePaid(BigDecimal pricePaid) { this.pricePaid = pricePaid; }

    public enum Status { NEEDED, IN_CART, PURCHASED, REMOVED }
}
