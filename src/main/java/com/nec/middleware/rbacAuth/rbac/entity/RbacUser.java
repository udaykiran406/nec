package com.nec.middleware.rbacAuth.rbac.entity;

import com.nec.middleware.Lookups.entity.Departments;
import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.entity.MasterDataRegion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

/**
 * Entity class representing the {@code nec_rbac_users} table.
 * Manages user definitions with their associated role, location, and profile info.
 *
 * <p>Keycloak-linked fields:
 * <ul>
 *   <li>{@code userId} – primary key; the Keycloak subject UUID assigned at user creation.</li>
 *   <li>{@code keycloakUserId} – same value as {@code userId}; kept for explicit Keycloak linkage.</li>
 *   <li>{@code status} – textual status ("ACTIVE" / "INACTIVE") aligned with Keycloak state.</li>
 *   <li>{@code locationId} – optional generic location reference for flexible location mapping.</li>
 * </ul>
 *
 * <p>Location FK fields (genderId, departmentId, regionId, districtId, cityId) are nullable
 * to allow partial user records created during the initial Keycloak sync.  The full profile
 * is populated later via the RBAC user management APIs.
 */
@Entity
@Table(
    name = "nec_rbac_users",
    uniqueConstraints = {
        //@UniqueConstraint(name = "uq_nec_rbac_users_phone",            columnNames = "phone"),
        @UniqueConstraint(name = "uq_nec_rbac_users_email",            columnNames = "email"),
        @UniqueConstraint(name = "uq_nec_rbac_users_keycloak_user_id", columnNames = "keycloak_user_id")
    },
    indexes = {
        @Index(name = "idx_nec_rbac_users_active",          columnList = "is_active"),
        @Index(name = "idx_nec_rbac_users_deleted",         columnList = "is_deleted"),
        @Index(name = "idx_users_role_id",                  columnList = "role_id"),
        @Index(name = "idx_users_region",                   columnList = "region_id"),
        @Index(name = "idx_users_city",                     columnList = "city_id"),
        @Index(name = "idx_users_gender_id",                columnList = "gender_id"),
        @Index(name = "idx_users_keycloak_user_id",         columnList = "keycloak_user_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RbacUser extends BaseAuditEntity implements Persistable<String> {

    // ==================== PRIMARY KEY ====================
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    /**
     * Marks entities built for insert so Spring Data JPA calls {@code persist()} instead of
     * {@code merge()} when the UUID is assigned before save (avoids Hibernate 6 session errors).
     */
    @Transient
    @Builder.Default
    private boolean newEntity = true;


    // ==================== KEYCLOAK INTEGRATION FIELDS ====================

    /**
     * Keycloak subject UUID — identical to {@code userId} for users created via the RBAC API.
     */
    @Column(name = "keycloak_user_id", length = 36, unique = true)
    private String keycloakUserId;

    /**
     * Textual account status ("ACTIVE" / "INACTIVE") mirroring Keycloak's enabled flag.
     * Defaults to "ACTIVE". Can differ from {@code isActive} during transition periods.
     */
    @Column(name = "status", length = 50)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // ==================== BUSINESS FIELDS ====================

    /** Full name of the user (max 150 chars). */
    @Column(name = "user_name", nullable = false, length = 150)
    private String userName;

    /** FK → nec_lkp_genders(id). Nullable to allow partial sync records. */
    @Column(name = "gender_id")
    private Long genderId;

     /** Gender lookup association for filtering and response enrichment. */
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "gender_id", referencedColumnName = "id", insertable = false, updatable = false)
     private Genders gender;

    /** FK → nec_rbac_roles(role_id). Nullable; assigned after sync when role is known. */
    @Column(name = "role_id")
    private Long roleId;

     /** Role association for filtering and response enrichment. */
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
     private RbacRole role;

    /** Unique phone number (max 20 chars). */
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /** Unique email address (max 150 chars). */
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** Optional path to the user's profile photo (max 500 chars). */
    @Column(name = "photo_path", length = 500)
    private String photoPath;

    /** FK → nec_lkp_departments(id). Nullable to allow partial sync records. */
    @Column(name = "department_id")
    private Long departmentId;

     /** Department lookup association for filtering and response enrichment. */
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "department_id", referencedColumnName = "id", insertable = false, updatable = false)
     private Departments department;

    /** FK → nec_regions(id). Nullable to allow partial sync records. */
    @Column(name = "region_id")
    private Long regionId;

     /** Region association for filtering and response enrichment. */
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "region_id", referencedColumnName = "id", insertable = false, updatable = false)
     private MasterDataRegion region;

    /** FK → nec_districts(id). Nullable to allow partial sync records. */
    @Column(name = "district_id")
    private Long districtId;

     /** District association for filtering and response enrichment. */
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "district_id", referencedColumnName = "id", insertable = false, updatable = false)
     private MasterDataDistrict district;

    /** FK → nec_cities(id). Nullable to allow partial sync records. */
    @Column(name = "city_id")
    private Long cityId;

     /** City association for filtering and response enrichment. */
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "city_id", referencedColumnName = "id", insertable = false, updatable = false)
     private MasterDataCity city;

    // ==================== STATUS / FLAGS ====================

    /** Whether this user account is active. Default: {@code 1} (active). */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Integer isActive = 1;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ==================== PASSWORD MANAGEMENT ====================

    @Column(name = "password_to_be_changed", nullable = false)
    @Builder.Default
    private Boolean passwordToBeChanged = false;

    @CreationTimestamp
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    // ==================== SECURITY / LOCKOUT ====================

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    // ==================== VERIFICATION ====================

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "mobile_verified", nullable = false)
    @Builder.Default
    private Boolean mobileVerified = false;

    // ==================== SOFT DELETE ====================

    /** Soft-delete flag: 0 = active, 1 = soft-deleted. */
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;

    @Override
    public String getId() {
        return userId;
    }

    @Override
    public boolean isNew() {
        return newEntity || userId == null;
    }

    @PostPersist
    @PostLoad
    private void markNotNew() {
        this.newEntity = false;
    }
}
