package com.mygroceries.backend.service;

import com.mygroceries.backend.model.PasswordResetToken;
import com.mygroceries.backend.model.User;
import com.mygroceries.backend.repo.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@Transactional
public class PasswordResetService {

    public static final String RESET_MESSAGE = "If an account exists for that email, a password reset link has been sent.";

    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetDeliveryService deliveryService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long expirationMinutes;
    private final String resetBaseUrl;

    public PasswordResetService(
            UserService userService,
            PasswordResetTokenRepository tokenRepository,
            PasswordResetDeliveryService deliveryService,
            @Value("${password-reset.expiration-minutes:45}") long expirationMinutes,
            @Value("${password-reset.frontend-url:http://localhost:5173/reset-password}") String resetBaseUrl
    ) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.deliveryService = deliveryService;
        this.expirationMinutes = expirationMinutes;
        this.resetBaseUrl = resetBaseUrl;
    }

    public String requestReset(String email) {
        userService.findByEmail(email).ifPresent(this::createResetToken);
        return RESET_MESSAGE;
    }

    public void resetPassword(String token, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        PasswordResetToken resetToken = tokenRepository.findByTokenHash(hashToken(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token"));

        LocalDateTime now = LocalDateTime.now();
        if (resetToken.getUsedAt() != null || resetToken.getExpiresAt().isBefore(now)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token");
        }

        userService.updatePassword(resetToken.getUser(), password);
        resetToken.setUsedAt(now);
    }

    private void createResetToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.findByUser_IdAndUsedAtIsNull(user.getId())
                .forEach(existing -> existing.setUsedAt(now));

        String rawToken = generateToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hashToken(rawToken));
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plusMinutes(expirationMinutes));

        tokenRepository.save(resetToken);

        deliveryService.sendResetLink(user, resetBaseUrl + "?token=" + rawToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
