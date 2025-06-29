package com.bank.notification.consumer;

import com.bank.notification.model.UserRegistrationEvent;
import com.bank.notification.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.consumer.topic.registered.user}", groupId = "${spring.kafka.consumer.group.name}")
    public void listenRegisteredUser(ConsumerRecord<String, Object> record) {
        try {
            log.info("Consumed registered user ::: {}", record);
            String jsonValue = (String) record.value();
            JsonNode rootNode = objectMapper.readTree(jsonValue);

            UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                    .id(Optional.ofNullable(rootNode.path("id").asText(null)).orElse(UUID.randomUUID().toString().replace("-", "")))
                    .name(rootNode.path("firstName").asText() + " " + rootNode.path("lastName").asText())
                    .email(rootNode.path("email").asText())
                    .build();
            notificationService.sendWelcomeEmail(userRegistrationEvent);
            log.info("Sending welcome email to user ::: {}", userRegistrationEvent.getEmail());
        } catch (Exception e) {
            log.error("An error occurred when consume record from kafka ::: {}", e.getMessage());
        }
    }
}
