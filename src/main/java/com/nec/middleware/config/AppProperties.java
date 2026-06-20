package com.nec.middleware.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        PasswordReset passwordReset,
        SmsService smsService,
        Cors cors
) {

    public record PasswordReset(
            String url,
            int tokenValidityMinutes
    ) {}

    public record SmsService(
            String baseUrl
    ) {}

    public record Cors(
            List<String> allowedOrigins
    ) {}
}
