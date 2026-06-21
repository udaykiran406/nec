package com.nec.middleware.rbacAuth.auth.mapper;

import com.nec.middleware.rbacAuth.auth.dto.response.NecUserContextDTO;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapper for converting RbacUser entities to NecUserContextDTO.
 *
 * Handles null-safe mapping, avoids lazy loading issues by mapping only
 * entity names (not relationships), and maintains clean builder patterns.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NecUserContextMapper {

    /**
     * Maps an RbacUser entity to NecUserContextDTO.
     *
     * @param user the RbacUser entity (must not be null)
     * @return the mapped NecUserContextDTO
     */
    public NecUserContextDTO toDTO(RbacUser user) {
        if (user == null) {
            log.warn("Null RbacUser passed to toDTO()");
            return null;
        }

        return NecUserContextDTO.builder()
                // Core Identity
                .userId(user.getUserId())

                // Business Identifiers
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())

                // Location (by NAME, not ID)
                .region(extractRegionName(user))
                .district(extractDistrictName(user))
                .city(extractCityName(user))

                // Roles (by NAME, not ID)
                .roles(new HashSet<>(extractRoleNames(user)))

                // Custom Attributes
                .attributes(Map.of())  // Can be extended for Keycloak attributes

                // Status & Flags
                .status(user.getStatus())
                .isActive(user.getIsActive())

                // Audit Fields
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())

                .build();
    }

    /**
     * Extracts region name from user's region relationship.
     * Avoids triggering lazy-loaded relationship unless already loaded.
     *
     * @param user the RbacUser entity
     * @return region name or null if not available
     */
    private String extractRegionName(RbacUser user) {
        if (user == null) {
            return null;
        }

        try {
            // Check if relationship is already loaded
            if (user.getRegion() != null && user.getRegion().getRegionName() != null) {
                return user.getRegion().getRegionName();
            }
        } catch (Exception e) {
            log.debug("Could not extract region name: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Extracts district name from user's district relationship.
     * Avoids triggering lazy-loaded relationship unless already loaded.
     *
     * @param user the RbacUser entity
     * @return district name or null if not available
     */
    private String extractDistrictName(RbacUser user) {
        if (user == null) {
            return null;
        }

        try {
            // Check if relationship is already loaded
            if (user.getDistrict() != null && user.getDistrict().getDistrictName() != null) {
                return user.getDistrict().getDistrictName();
            }
        } catch (Exception e) {
            log.debug("Could not extract district name: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Extracts city name from user's city relationship.
     * Avoids triggering lazy-loaded relationship unless already loaded.
     *
     * @param user the RbacUser entity
     * @return city name or null if not available
     */
    private String extractCityName(RbacUser user) {
        if (user == null) {
            return null;
        }

        try {
            // Check if relationship is already loaded
            if (user.getCity() != null && user.getCity().getCityName() != null) {
                return user.getCity().getCityName();
            }
        } catch (Exception e) {
            log.debug("Could not extract city name: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Extracts role names from user's role.
     * Maps only the role name, not the role ID.
     *
     * @param user the RbacUser entity
     * @return list of role names, or empty list if no role assigned
     */
    private List<String> extractRoleNames(RbacUser user) {
        if (user == null) {
            return Collections.emptyList();
        }

        try {
            // Check if role is assigned and already loaded
            if (user.getRole() != null && user.getRole().getRoleName() != null) {
                return List.of(user.getRole().getRoleName());
            }
        } catch (Exception e) {
            log.debug("Could not extract role name: {}", e.getMessage());
        }

        return Collections.emptyList();
    }
}

