package com.bank.notification.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class EnvironmentParamConfig {

    @Value("${springdoc.info.title}")
    private String springdocTitle;

    @Value("${springdoc.info.description}")
    private String springdocDescription;

    @Value("${springdoc.info.version}")
    private String springdocVersion;

    @Value("${springdoc.info.contact.name}")
    private String springdocContactName;

    @Value("${springdoc.info.contact.email}")
    private String springdocContactEmail;

    @Value("${springdoc.info.contact.url}")
    private String springdocContactUrl;

    @Value("${springdoc.info.license.name}")
    private String springdocLicenseName;

    @Value("${springdoc.info.license.url}")
    private String springdocLicenseUrl;

    @Value("${springdoc.info.external.docs.description}")
    private String springdocExternalDocsDescription;

    @Value("${springdoc.info.external.docs.url}")
    private String springdocExternalDocsUrl;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapSevers;

    @Value("${spring.kafka.properties.security.protocol}")
    private String kafkaSecurityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String kafkaSaslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String kafkaJaasConfig;

    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String kafkaTruststoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.password}")
    private String kafkaTruststorePassword;

    @Value("${spring.kafka.producer.user.pending.registration.topic}")
    private String kafkaUserPendingRegistrationTopic;

    @Value("${spring.kafka.producer.user.success.registration.topic}")
    private String kafkaUserSuccessRegistrationTopic;

    @Value("${spring.mail.host}")
    private String smtpEmailHost;

    @Value("${spring.mail.port}")
    private String smtpEmailPort;

    @Value("${spring.mail.username}")
    private String smtpEmailUsername;

    @Value("${spring.mail.password}")
    private String smtpEmailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpEmailAuth;

    @Value("${spring.kafka.consumer.topic.registered.user}")
    private String kafkaRegisteredUserTopic;

    @Value("${spring.kafka.consumer.group.name}")
    private String kafkaGroupName;

}
