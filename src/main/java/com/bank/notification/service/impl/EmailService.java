package com.bank.notification.service.impl;

import com.bank.common.lib.exception.CommonCustomException;
import com.bank.common.lib.utils.Constants;
import com.bank.notification.config.EnvironmentParamConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private EnvironmentParamConfig environmentParamConfig;

    public boolean sendEmail(String recipient, String subject, String emailBody) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", environmentParamConfig.getSmtpEmailHost());
            props.put("mail.smtp.port", environmentParamConfig.getSmtpEmailPort());
            props.put("mail.smtp.auth", environmentParamConfig.getSmtpEmailAuth());

            // Determine whether to use SSL or STARTTLS
            if ("465".equals(environmentParamConfig.getSmtpEmailPort())) {
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.starttls.enable", "false");
            } else if ("587".equals(environmentParamConfig.getSmtpEmailPort())) {
                props.put("mail.smtp.ssl.enable", "false");
                props.put("mail.smtp.starttls.enable", "true");
            }

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(environmentParamConfig.getSmtpEmailUsername(), environmentParamConfig.getSmtpEmailPassword());
                }
            });
            session.setDebug(true);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(environmentParamConfig.getSmtpEmailUsername()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setContent(emailBody, "text/html; charset=UTF-8");

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            throw new CommonCustomException(Constants.INTERNAL_SERVER_ERROR_STATUS_CODE, "Failed to send email to " + recipient + ": " + e.getMessage());
        }
    }
}
