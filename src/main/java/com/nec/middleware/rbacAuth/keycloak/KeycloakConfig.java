package com.nec.middleware.rbacAuth.keycloak;

import com.nec.middleware.rbacAuth.keycloak.client.KeycloakClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configures the declarative {@link KeycloakClient} HTTP proxy and validates Keycloak
 * properties at startup so misconfiguration fails fast.
 */
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
@Slf4j
public class KeycloakConfig {

    @Bean
    ApplicationRunner keycloakPropertiesValidator(KeycloakProperties keycloakProperties) {
        return args -> {
            keycloakProperties.validate();
            log.info("Keycloak configured: baseUrl={}, realm={}, adminRealm={}, " +
                            "adminClientId={}, adminUsername={}, grantType={}, " +
                            "webClientId={} — adminPassword=[REDACTED]",
                    keycloakProperties.requireBaseUrl(),
                    keycloakProperties.realmName(),
                    keycloakProperties.requireAdminRealm(),
                    keycloakProperties.adminClientId(),
                    keycloakProperties.adminUsername(),
                    keycloakProperties.grantType(),
                    keycloakProperties.webClientId());
        };
    }

    @Bean
    public KeycloakClient keycloakClient(KeycloakProperties keycloakProperties) {
        String baseUrl = keycloakProperties.requireBaseUrl();

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(KeycloakClient.class);
    }
}
