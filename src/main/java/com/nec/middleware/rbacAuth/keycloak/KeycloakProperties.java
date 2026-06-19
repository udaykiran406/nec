package com.nec.middleware.rbacAuth.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Keycloak connection settings bound from {@code keycloak.*} in application.yaml.
 *
 * <p>Property mapping:
 * <ul>
 *   <li>{@code keycloak.base-url} – required; server root, e.g. {@code http://localhost:8080}.</li>
 *   <li>{@code keycloak.server-url} – deprecated alias accepted for backward compatibility.</li>
 *   <li>{@code keycloak.admin-realm} – realm used for admin token; defaults to {@code master}.</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "keycloak")
public record KeycloakProperties(
        String baseUrl,
        String serverUrl,
        String realmName,
        String adminRealm,
        String grantType,
        String adminClientId,
        String adminClientSecret,
        String adminUsername,
        String adminPassword,
        String webClientId,
        String webClientSecret
) {

    /**
     * Returns the normalized Keycloak base URL (no trailing slash).
     * Accepts either {@code base-url} or the deprecated {@code server-url} property.
     *
     * @throws IllegalStateException if the URL is absent or has no HTTP/HTTPS scheme
     */
    public String requireBaseUrl() {
        String url = StringUtils.hasText(baseUrl) ? baseUrl.trim()
                : StringUtils.hasText(serverUrl) ? serverUrl.trim() : null;

        if (!StringUtils.hasText(url)) {
            throw new IllegalStateException(
                    "Keycloak base URL is not configured. "
                            + "Set keycloak.base-url (e.g. http://localhost:8080) in application.yaml");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalStateException(
                    "Keycloak base URL must include a scheme (http:// or https://). "
                            + "Current value: '" + url + "'");
        }

        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Returns the realm used for admin token requests.
     * Defaults to {@code master} if {@code keycloak.admin-realm} is not set.
     */
    public String requireAdminRealm() {
        return StringUtils.hasText(adminRealm) ? adminRealm.trim() : "master";
    }

    /**
     * Validates all required Keycloak properties at startup.
     *
     * @throws IllegalStateException if any required property is missing
     */
    public void validate() {
        requireBaseUrl();

        if (!StringUtils.hasText(realmName)) {
            throw new IllegalStateException("keycloak.realm-name must be configured");
        }
        if (!StringUtils.hasText(grantType)) {
            throw new IllegalStateException("keycloak.grant-type must be configured");
        }
        if (!StringUtils.hasText(adminClientId)) {
            throw new IllegalStateException("keycloak.admin-client-id must be configured");
        }
        if (!StringUtils.hasText(adminUsername)) {
            throw new IllegalStateException("keycloak.admin-username must be configured");
        }
        if ("password".equalsIgnoreCase(grantType) && !StringUtils.hasText(adminPassword)) {
            throw new IllegalStateException(
                    "keycloak.admin-password is required when keycloak.grant-type is 'password'");
        }
        if (!StringUtils.hasText(webClientId)) {
            throw new IllegalStateException("keycloak.web-client-id must be configured");
        }
        if (!StringUtils.hasText(webClientSecret)) {
            throw new IllegalStateException("keycloak.web-client-secret must be configured");
        }
    }
}
