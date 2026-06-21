package com.nec.middleware.rbacAuth.keycloak.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ClientCredentials {
    private String clientId;
    private String clientSecret;
}
