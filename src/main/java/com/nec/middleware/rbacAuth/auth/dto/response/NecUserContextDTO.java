package com.nec.middleware.rbacAuth.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NecUserContextDTO {

    // Core Identity (Keycloak userId)
    private String userId;           // Keycloak UUID (PK)

    // Business Identifiers
    private String userName;
    private String email;
    private String phone;

    // Location
    private Long regionId;
    private Long districtId;
    private Long cityId;

    // Status & Audit
    private String status;           // ACTIVE, INACTIVE, SUSPENDED
    private Integer isActive;

    // Roles & Permissions (JSON/String for other services)
    private Long roleId;
    private Set<String> roles;       // ["ROLE_USER", "ROLE_ADMIN", etc.]
    private Set<Long> roleIds;       // [1, 2, 3] - for UI and internal use

    // Extra attributes (extendable)
    private Map<String, Object> attributes;

    // Audit Fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private String updatedBy;

    // Token Context (optional)
    private String sessionId;
    private Long tokenExpiry;
}
