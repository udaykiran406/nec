package com.nec.middleware.rbacAuth.rbac.dto.response;

import com.nec.middleware.rbacAuth.rbac.dto.response.nested.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacUserResponse {

    private String id;
    private String keycloakUserId;
    private String userName;
    private GenderResponse gender;
    private RoleResponse role;
    private String phone;
    private String email;
    private String photoPath;
    private DepartmentResponse department;
    private RegionResponse region;
    private DistrictResponse district;
    private CityResponse city;
    private Integer isActive;

    /** Whether the user must change their password on next login. */
    private Boolean passwordToBeChanged;

    /** Whether the user's email address has been verified. */
    private Boolean emailVerified;

    /** Whether the user's mobile number has been verified. */
    private Boolean mobileVerified;

    /** Timestamp of the user's most recent successful login. */
    private LocalDateTime lastLoginAt;

    /** Number of consecutive failed login attempts since last success. */
    private Integer failedLoginAttempts;
}
