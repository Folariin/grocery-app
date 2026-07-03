package com.mygroceries.backend.service;

import com.mygroceries.backend.model.User;
import com.mygroceries.backend.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    public User signup(String email, String displayName, String rawPassword) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException();
        }
        User u = new User();
        u.setEmail(email.trim());
        u.setDisplayName(displayName.trim());
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setStatus("ACTIVE");
        return users.save(u);
    }

    public Optional<User> findByEmail(String email) {
        return users.findByEmailIgnoreCase(email.trim());
    }

    public User getById(UUID userId) {
        return users.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public boolean passwordMatches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }

    public void updatePassword(User user, String rawPassword) {
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setUpdatedAt(LocalDateTime.now());
        users.save(user);
    }

    public User updateDisplayName(UUID userId, String displayName) {
        String trimmed = displayName.trim();
        if (trimmed.length() < 2 || trimmed.length() > 60) {
            throw new InvalidDisplayNameException();
        }

        User user = getById(userId);
        user.setDisplayName(trimmed);
        user.setUpdatedAt(LocalDateTime.now());
        return users.save(user);
    }

    // custom exceptions
    public static class EmailAlreadyUsedException extends RuntimeException {}
    public static class InvalidCredentialsException extends RuntimeException {}
    public static class UserNotFoundException extends RuntimeException {}
    public static class InvalidDisplayNameException extends RuntimeException {}
}
