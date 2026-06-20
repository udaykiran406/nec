package com.nec.middleware.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class MailjetConfig {

    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.api.secret}")
    private String apiSecret;

    @PostConstruct
    void validateCredentials() {
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(apiSecret)) {
            throw new IllegalStateException(
                    "Mailjet API credentials are not configured. Set MAILJET_API_KEY and MAILJET_API_SECRET.");
        }
    }

    @Bean
    public MailjetClient mailjetClient() {
        log.info("Initializing Mailjet client...");

        ClientOptions options = ClientOptions.builder()
                .apiKey(apiKey)
                .apiSecretKey(apiSecret)
                .build();

        MailjetClient client = new MailjetClient(options);

        log.info("Mailjet client initialized successfully");
        return client;
    }
}
