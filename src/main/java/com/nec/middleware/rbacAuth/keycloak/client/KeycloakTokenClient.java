package com.nec.middleware.rbacAuth.keycloak.client;

import com.nec.middleware.rbacAuth.keycloak.KeycloakProperties;
import com.nec.middleware.rbacAuth.keycloak.util.KeycloakFormData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * Posts form-urlencoded requests to Keycloak token endpoints using an imperative
 * {@link RestClient} with an explicitly encoded body.
 */
@Component
@RequiredArgsConstructor
public class KeycloakTokenClient {

    private final KeycloakProperties keycloakProperties;

    public ResponseEntity<Object> postTokenForm(String realm, MultiValueMap<String, String> formData) {
        return postForm("/realms/{realm}/protocol/openid-connect/token", realm, formData);
    }

    public ResponseEntity<Object> postLogoutForm(String realm, MultiValueMap<String, String> formData) {
        return postForm("/realms/{realm}/protocol/openid-connect/logout", realm, formData);
    }

    public ResponseEntity<Object> postIntrospectForm(String realm, MultiValueMap<String, String> formData) {
        return postForm("/realms/{realm}/protocol/openid-connect/token/introspect", realm, formData);
    }

    private ResponseEntity<Object> postForm(String path, String realm, MultiValueMap<String, String> formData) {
        return restClient()
                .post()
                .uri(path, realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(KeycloakFormData.toEncodedString(formData))
                .retrieve()
                .toEntity(Object.class);
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(keycloakProperties.requireBaseUrl())
                .build();
    }
}
