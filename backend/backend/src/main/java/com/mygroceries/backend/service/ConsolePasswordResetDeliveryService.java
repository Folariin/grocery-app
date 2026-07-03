package com.mygroceries.backend.service;

import com.mygroceries.backend.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "password-reset.delivery-mode", havingValue = "console", matchIfMissing = true)
public class ConsolePasswordResetDeliveryService implements PasswordResetDeliveryService {

    private final String appEnvironment;

    public ConsolePasswordResetDeliveryService(@Value("${app.environment:local}") String appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    @PostConstruct
    void validateEnvironment() {
        if (isProduction()) {
            throw new IllegalStateException("Console password reset delivery is disabled in production. Configure an email delivery implementation before deploying.");
        }
    }

    @Override
    public void sendResetLink(User user, String resetLink) {
        System.out.println("Password reset link: " + resetLink);
    }

    private boolean isProduction() {
        return "production".equalsIgnoreCase(appEnvironment) || "prod".equalsIgnoreCase(appEnvironment);
    }
}
