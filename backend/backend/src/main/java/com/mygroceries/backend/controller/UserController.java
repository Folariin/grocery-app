package com.mygroceries.backend.controller;

import com.mygroceries.backend.dto.UserDtos.UpdateProfileRequest;
import com.mygroceries.backend.dto.UserDtos.UserProfileResponse;
import com.mygroceries.backend.model.User;
import com.mygroceries.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication auth) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(toProfileResponse(userService.getById(userId)));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            Authentication auth,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        User updated = userService.updateDisplayName(userId, req.displayName());
        return ResponseEntity.ok(toProfileResponse(updated));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail()
        );
    }
}
