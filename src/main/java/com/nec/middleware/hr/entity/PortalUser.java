package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.Lookups.entity.PortalUserTypes;
import com.nec.middleware.Lookups.entity.Roles;
import com.nec.middleware.hr.Enum.MasterData;
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

    @Column(name = "phone", nullable = false, unique = true,length = 20)
    private String phone;

    @Column(name = "email", nullable = false,unique = true,length = 150)
    private String email;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "faculty", length = 100)
    private String faculty;


    // ------------------------------------------------------------------ LOOKUP FKs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false)
    private Genders gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_user_type_id")
    private PortalUserTypes portalUserType;

    @Enumerated(EnumType.STRING)
    @Column(name = "master_data_type", nullable = false)
    private MasterData masterData;

    @Column(name="masterdata_id",nullable = false)
    private Long masterdataId;
    // ------------------------------------------------------------------ MASTER DATA FKs



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private MasterDataRegion region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private MasterDataDistrict district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MasterDataCity city;
}