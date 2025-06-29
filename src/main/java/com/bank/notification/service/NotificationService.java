package com.bank.notification.service;

import com.bank.notification.model.UserRegistrationEvent;

public interface NotificationService {
    void sendWelcomeEmail(UserRegistrationEvent userRegistrationEvent);

    void sendWelcomeSms(String email, String name);
}
