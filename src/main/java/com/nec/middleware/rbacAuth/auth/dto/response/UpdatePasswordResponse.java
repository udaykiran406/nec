package com.nec.middleware.rbacAuth.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePasswordResponse {
    private String message;
    private boolean passwordUpdated;
}
