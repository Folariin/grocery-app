package com.mygroceries.backend.service;

import com.mygroceries.backend.model.User;
import com.mygroceries.backend.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public boolean passwordMatches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }

    // custom exceptions
    public static class EmailAlreadyUsedException extends RuntimeException {}
    public static class InvalidCredentialsException extends RuntimeException {}
}
