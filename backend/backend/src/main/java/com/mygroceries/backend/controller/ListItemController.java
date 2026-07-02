package com.mygroceries.backend.controller;

import com.mygroceries.backend.dto.ListItemDtos.*;
import com.mygroceries.backend.service.ListItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ListItemController {

    private final ListItemService listItemService;

    public ListItemController(ListItemService listItemService) {
        this.listItemService = listItemService;
    }

    @PostMapping("/api/lists/{listId}/items")
    public ResponseEntity<ListItemResponse> addItem(
            Authentication auth,
            @PathVariable UUID listId,
            @Valid @RequestBody AddListItemRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        ListItemResponse created = listItemService.addItem(
                userId, listId, req.itemName(), req.quantity(), req.unit(), req.notes()
        );
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/api/lists/{listId}/items")
    public ResponseEntity<List<ListItemResponse>> getItems(
            Authentication auth,
            @PathVariable UUID listId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(listItemService.getItems(userId, listId));
    }

    @PatchMapping("/api/list-items/{listItemId}/purchase")
    public ResponseEntity<ListItemResponse> setPurchased(
            Authentication auth,
            @PathVariable UUID listItemId,
            @Valid @RequestBody PurchaseRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(listItemService.setPurchased(userId, listItemId, req.purchased()));
    }

    @DeleteMapping("/api/list-items/{listItemId}")
    public ResponseEntity<?> removeItem(
            Authentication auth,
            @PathVariable UUID listItemId
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        listItemService.removeItem(userId, listItemId);
        return ResponseEntity.noContent().build();
    }
}
