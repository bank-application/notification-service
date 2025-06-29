package com.bank.notification.service.impl;

import com.bank.common.lib.exception.CommonCustomException;
import com.bank.common.lib.utils.Constants;
import com.bank.notification.model.UserRegistrationEvent;
import com.bank.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendWelcomeEmail(UserRegistrationEvent userRegistrationEvent) {
        try {
            String subject = "Welcome to SmartBank – Your Account Is Now Active";

            // Build dynamic HTML using Thymeleaf
            Context context = new Context();
            context.setVariable("name", userRegistrationEvent.getName());
            context.setVariable("email", userRegistrationEvent.getEmail());

            String htmlContent = templateEngine.process("welcome-email", context);
            emailService.sendEmail(userRegistrationEvent.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            throw new CommonCustomException(Constants.INTERNAL_SERVER_ERROR_STATUS_CODE, e.getMessage());
        }
    }

    @Override
    public void sendWelcomeSms(String email, String name) {

    }
}
