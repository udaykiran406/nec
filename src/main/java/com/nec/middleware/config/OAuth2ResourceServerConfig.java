package com.nec.middleware.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * OAuth2 Resource Server configuration for validating Keycloak JWT tokens.
 *
 * This allows Spring Security to decode and validate JWT tokens issued by Keycloak,
 * enabling the `.authenticated()` requirement in SecurityConfig to work properly.
 *
 * Configuration is done via application.yaml properties:
 *   spring.security.oauth2.resourceserver.jwt.issuer-uri: http://localhost:8080/realms/NEC-DEV
 *   spring.security.oauth2.resourceserver.jwt.jwk-set-uri: http://localhost:8080/realms/NEC-DEV/protocol/openid-connect/certs
 *
 * Spring Security's autoconfiguration automatically:
 * - Creates a JwtDecoder bean from the jwk-set-uri
 * - Validates JWT signatures using Keycloak's public keys
 * - Validates token expiration and issuer claim
 *
 * The SecurityConfig then uses the JwtDecoder via:
 *   .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
 *
 * This integrates with the custom @Authorize AOP pattern:
 * 1. Spring Security validates JWT at the filter level
 * 2. Request reaches the controller method
 * 3. Custom @Authorize AOP validates local RBAC roles/permissions
 */
@Configuration
@Slf4j
public class OAuth2ResourceServerConfig {

    /**
     * Note: JwtDecoder bean is auto-configured by Spring Security
     * from the jwk-set-uri property. No explicit bean definition needed.
     */
}

