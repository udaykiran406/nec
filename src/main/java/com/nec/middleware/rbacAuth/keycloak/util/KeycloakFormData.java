package com.nec.middleware.rbacAuth.keycloak.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Builds {@code application/x-www-form-urlencoded} bodies for Keycloak token endpoints.
 */
public final class KeycloakFormData {

    private KeycloakFormData() {
    }

    public static MultiValueMap<String, String> passwordGrant(
            String clientId,
            String clientSecret,
            String grantType,
            String username,
            String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", grantType);
        form.add("username", username);
        form.add("password", password);
        return form;
    }

    public static MultiValueMap<String, String> adminPasswordGrant(
            String clientId,
            String grantType,
            String username,
            String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("grant_type", grantType);
        form.add("username", username);
        form.add("password", password);
        return form;
    }

    public static MultiValueMap<String, String> refreshGrant(
            String clientId,
            String clientSecret,
            String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        return form;
    }

    public static MultiValueMap<String, String> introspect(
            String clientId,
            String clientSecret,
            String token) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("token", token);
        return form;
    }

    public static MultiValueMap<String, String> logout(
            String clientId,
            String clientSecret,
            String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        return form;
    }

    /** Serializes form fields into a URL-encoded POST body string. */
    public static String toEncodedString(MultiValueMap<String, String> form) {
        return form.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> encode(entry.getKey()) + "=" + encode(value)))
                .collect(Collectors.joining("&"));
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
