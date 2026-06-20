package com.nec.middleware.rbacAuth.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakErrorResponse {

    private String error;
    private String error_description;
    private String errorMessage;
    private Integer status = 401;

    public String getErrorDescription() {
        return error_description;
    }

    public void setErrorDescription(String error_description) {
        this.error_description = error_description;
    }
}
