package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.Lookups.entity.PortalUserTypes;
import com.nec.middleware.Lookups.entity.Roles;
import com.nec.middleware.masterdata.entity.*;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: nec_hr_portal_users
 *
 * FK associations:
 *  - genderId       → nec_lkp_gender         (Lookup table)
 *  - roleId         → nec_lkp_roles           (Lookup table)
 *  - portalUserTypeId → nec_lkp_portal_user_types (Lookup table)
 *  - universityId   → nec_universities        (Master data)
 *  - regionId       → nec_regions             (Master data)
 *  - districtId     → nec_districts           (Master data)
 *  - cityId         → nec_cities              (Master data)
 *
 * Audit fields inherited from {@link AuditableEntity}.
 */
@Entity
@Table(name = "nec_hr_portal_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortalUser extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "portal_user_id", nullable = false, unique = true, length = 20)
    private String portalUserId;

    @Column(name = "user_name", nullable = false, length = 150)
    private String userName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "faculty", length = 100)
    private String faculty;

    // ------------------------------------------------------------------ LOOKUP FKs

    /**
     * FK → nec_lkp_gender.id
     * Stored column: gender_id
     */
    @Column(name = "gender_id", nullable = false, insertable = false, updatable = false)
    private Long genderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false)
    private Genders gender;

    /**
     * FK → nec_lkp_roles.id
     * Stored column: role_id
     */
    @Column(name = "role_id", nullable = false, insertable = false, updatable = false)
    private Long roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    /**
     * FK → nec_lkp_portal_user_types.id
     * Stored column: portal_user_type_id
     */
    @Column(name = "portal_user_type_id", insertable = false, updatable = false)
    private Long portalUserTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_user_type_id")
    private PortalUserTypes portalUserType;

    // ------------------------------------------------------------------ MASTER DATA FKs

    /**
     * FK → nec_universities.id
     * Stored column: university_id
     */
    @Column(name = "university_id", nullable = false, insertable = false, updatable = false)
    private Long universityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private MasterDataUniversity university;

    /**
     * FK → nec_regions.id
     * Stored column: region_id
     */
    @Column(name = "region_id", nullable = false, insertable = false, updatable = false)
    private Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private MasterDataRegion region;

    /**
     * FK → nec_districts.id
     * Stored column: district_id
     */
    @Column(name = "district_id", nullable = false, insertable = false, updatable = false)
    private Long districtId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private MasterDataDistrict district;

    /**
     * FK → nec_cities.id
     * Stored column: city_id
     */
    @Column(name = "city_id", nullable = false, insertable = false, updatable = false)
    private Long cityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MasterDataCity city;
}