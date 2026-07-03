package com.mygroceries.backend.service;

import com.mygroceries.backend.model.User;

public interface PasswordResetDeliveryService {
    void sendResetLink(User user, String resetLink);
}
