package com.mygroceries.backend.service;

import com.mygroceries.backend.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@ConditionalOnProperty(name = "password-reset.delivery-mode", havingValue = "resend")
public class ResendPasswordResetDeliveryService implements PasswordResetDeliveryService {

    private static final URI RESEND_EMAILS_URI = URI.create("https://api.resend.com/emails");

    private final HttpClient httpClient;
    private final String apiKey;
    private final String fromEmail;
    private final String fromName;

    public ResendPasswordResetDeliveryService(
            @Value("${password-reset.resend.api-key:}") String apiKey,
            @Value("${password-reset.resend.from-email:}") String fromEmail,
            @Value("${password-reset.resend.from-name:Grocery App}") String fromName
    ) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    @PostConstruct
    void validateConfiguration() {
        if (isBlank(apiKey)) {
            throw new IllegalStateException("RESEND_API_KEY is required when PASSWORD_RESET_DELIVERY_MODE=resend");
        }

        if (isBlank(fromEmail)) {
            throw new IllegalStateException("RESEND_FROM_EMAIL is required when PASSWORD_RESET_DELIVERY_MODE=resend");
        }
    }

    @Override
    public void sendResetLink(User user, String resetLink) {
        String payload = buildPayload(user, resetLink);

        HttpRequest request = HttpRequest.newBuilder(RESEND_EMAILS_URI)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Resend password reset email failed with status " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Password reset email delivery was interrupted", e);
        } catch (IOException e) {
            throw new IllegalStateException("Password reset email delivery failed", e);
        }
    }

    private String buildPayload(User user, String resetLink) {
        String subject = "Reset your Grocery App password";
        String text = "Use this link to reset your Grocery App password: " + resetLink;
        String html = "<p>Use this link to reset your Grocery App password:</p>"
                + "<p><a href=\"" + htmlEscape(resetLink) + "\">Reset password</a></p>"
                + "<p>If you did not request this, you can ignore this email.</p>";

        return "{"
                + "\"from\":\"" + jsonEscape(formatFrom()) + "\","
                + "\"to\":[\"" + jsonEscape(user.getEmail()) + "\"],"
                + "\"subject\":\"" + jsonEscape(subject) + "\","
                + "\"text\":\"" + jsonEscape(text) + "\","
                + "\"html\":\"" + jsonEscape(html) + "\""
                + "}";
    }

    private String formatFrom() {
        if (isBlank(fromName)) {
            return fromEmail.trim();
        }

        return fromName.trim() + " <" + fromEmail.trim() + ">";
    }

    private String jsonEscape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (c < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
                }
            }
        }
        return escaped.toString();
    }

    private String htmlEscape(String value) {
        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
