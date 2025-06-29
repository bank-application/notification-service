package com.bank.notification.controller;

import com.bank.common.lib.exception.CommonCustomException;
import com.bank.common.lib.model.response.CommonSuccessResponse;
import com.bank.common.lib.utils.Constants;
import com.bank.common.lib.utils.MetadataContext;
import com.bank.common.lib.utils.Utilities;
import com.bank.notification.config.EnvironmentParamConfig;
import com.bank.notification.model.UserRegistrationEvent;
import com.bank.notification.service.NotificationService;
import com.bank.notification.service.impl.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Log4j2
@RestController
@RequestMapping("/api/v1/bank/notification")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EnvironmentParamConfig environmentParamConfig;

    @Operation(summary = "Sent email", description = "This API is used to sending a email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/sent-email")
    public ResponseEntity<CommonSuccessResponse<Map<String, Object>>> sentEmail(@RequestBody UserRegistrationEvent userRegistrationEvent) {
        notificationService.sendWelcomeEmail(userRegistrationEvent);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Email sent successfully");
        return getSpecificResponse("Email sent successfully", Constants.OK_STATUS_CODE, response);
    }

    @Operation(summary = "Check service status", description = "This API is used to check health of service and it's components.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service status fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status")
    public ResponseEntity<CommonSuccessResponse<Map<String, Object>>> getStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        Map<String, Object> components = new LinkedHashMap<>();

        components.put("kafka", getKafkaBrokerStatus());

        long upCount = components.values().stream().filter("UP"::equals).count();
        long total = components.size();

        String overallStatus;
        if (upCount == total) {
            overallStatus = "UP";
        } else if (upCount == 0) {
            overallStatus = "DOWN";
        } else {
            overallStatus = "DEGRADED";
        }

        status.put("status", overallStatus);
        status.put("uptime", getUptime());
        status.put("components", components);
        return getSpecificResponse("Service status fetched successfully", Constants.OK_STATUS_CODE, status);
    }

    private String getUptime() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = runtimeMxBean.getUptime();

        Duration duration = Duration.ofMillis(uptimeMillis);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    private Map<String, Object> getKafkaBrokerStatus() {
        Map<String, Object> brokerStatus = new LinkedHashMap<>();
        String[] brokers = environmentParamConfig.getKafkaBootstrapSevers().split(",");
        for (String broker : brokers) {
            Properties properties = new Properties();
            properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, broker.trim());
            properties.put("security.protocol", environmentParamConfig.getKafkaSecurityProtocol());
            properties.put("sasl.mechanism", environmentParamConfig.getKafkaSaslMechanism());
            properties.put("sasl.jaas.config", environmentParamConfig.getKafkaJaasConfig());
            properties.put("ssl.truststore.location", environmentParamConfig.getKafkaTruststoreLocation());
            properties.put("ssl.truststore.password", environmentParamConfig.getKafkaTruststorePassword());

            try (AdminClient adminClient = AdminClient.create(properties)) {
                adminClient.describeCluster().clusterId().get();
                brokerStatus.put(broker.trim(), "UP");
            } catch (Exception e) {
                brokerStatus.put(broker.trim(), "DOWN");
            }
        }
        return brokerStatus;
    }

    private <T> ResponseEntity<CommonSuccessResponse<T>> getSpecificResponse(String msg, int statusCode, T payload) {
        try {
            CommonSuccessResponse<T> response = CommonSuccessResponse.<T>builder()
                    .timestamp(String.valueOf(LocalDateTime.now()))
                    .status(Constants.SUCCESS_TAG)
                    .statusCode(statusCode)
                    .message(msg)
                    .metadata(MetadataContext.getMetadata())
                    .payload(payload)
                    .build();
            log.info("{} ::: {}", msg, Utilities.objectToJsonString(response));
            return ResponseEntity.status(statusCode).body(response);
        } catch (Exception e) {
            throw new CommonCustomException(Constants.INTERNAL_SERVER_ERROR_STATUS_CODE, "Internal server error");
        }
    }
}
