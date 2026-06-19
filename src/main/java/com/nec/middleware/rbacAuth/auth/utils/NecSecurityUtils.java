package com.nec.middleware.rbacAuth.auth.utils;

import com.nec.middleware.rbacAuth.auth.dto.response.NecUserContextDTO;
import com.nec.middleware.rbacAuth.auth.exception.SomethingWentWrongException;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Set;

/**
 * Utility for reading the authenticated {@link RbacUser} out of Spring's
 * {@link SecurityContextHolder} — populated by {@link NecAuthorizeAspect}.
 *
 * <p>Provides methods to access the current authenticated user context within the application.
 */
public final class NecSecurityUtils {

    private NecSecurityUtils() {}

    /**
     * Returns the currently authenticated {@link RbacUser}.
     *
     * @throws IllegalStateException if the security context holds no RbacUser principal
     */
    public static RbacUser getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof RbacUser user)) {
            throw new IllegalStateException("Invalid security context – expected RbacUser principal");
        }
        return user;
    }

    /**
     * Builds a {@link NecUserContextDTO} from the current authenticated user.
     *
     * <p>This method extracts all relevant user information from the authenticated {@link RbacUser}
     * and constructs a comprehensive context DTO that can be used throughout the application.
     *
     * @return A complete user context DTO with all user information
     * @throws IllegalStateException if no user is authenticated
     * @throws SomethingWentWrongException if an error occurs during context building
     */
    public static NecUserContextDTO getUserContext() {
        try {
            RbacUser user = getCurrentUser();

            // Build roles set from the user's role
            Set<String> roles = Set.of();
            if (user.getRole() != null && user.getRole().getRoleCode() != null) {
                roles = Set.of(user.getRole().getRoleCode());
            }

            return NecUserContextDTO.builder()
                    // Core Identity
                    .userId(user.getUserId())

                    // Business Identifiers
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .phone(user.getPhone())

                    // Location
                    .regionId(user.getRegionId())
                    .districtId(user.getDistrictId())
                    .cityId(user.getCityId())

                    // Status & Audit
                    .status(user.getStatus())
                    .isActive(user.getIsActive())

                    // Roles & Permissions
                    .roleId(user.getRoleId())
                    .roles(roles)
                    .roleIds(user.getRoleId() != null ? Set.of(user.getRoleId()) : Set.of())

                    // Attributes and Audit fields
                    .attributes(Map.of())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .createdBy(user.getCreatedBy())
                    .updatedBy(user.getUpdatedBy())

                    // Token context (optional)
                    .sessionId(null)       // can be filled from token if needed
                    .tokenExpiry(null)     // can be filled from token introspection

                    .build();

        } catch (Exception e) {
            if (e instanceof IllegalStateException illegalEx) {
                throw new IllegalStateException(illegalEx.getMessage());
            }
            throw new SomethingWentWrongException("getUserContext()", e.getMessage());
        }
    }
}
