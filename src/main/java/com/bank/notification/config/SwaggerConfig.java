package com.bank.notification.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Autowired
    private EnvironmentParamConfig env;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title(env.getSpringdocTitle())
                        .description(env.getSpringdocDescription())
                        .version(env.getSpringdocVersion())
                        .contact(new Contact()
                                .name(env.getSpringdocContactName())
                                .email(env.getSpringdocContactEmail())
                                .url(env.getSpringdocContactUrl()))
                        .license(new License().name(env.getSpringdocLicenseName()).url(env.getSpringdocLicenseUrl())))
                .externalDocs(new ExternalDocumentation()
                        .description(env.getSpringdocExternalDocsDescription())
                        .url(env.getSpringdocExternalDocsUrl()));
    }
}
