package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.Lookups.entity.ThirdPartyStatus;
import com.nec.middleware.masterdata.entity.MasterDataAaqilType;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.entity.MasterDataRegion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_hr_aaqils")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aaqil extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key — generated once at creation, never changed. e.g. AA001 */
    @Column(name = "aaqil_id", nullable = false, unique = true, length = 20)
    private String aaqilId;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "age")
    private Short age;

    @Column(name = "phone", nullable = false, unique = true, length = 30)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    // ------------------------------------------------------------------ LOOKUPS FKs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aaqil_type_id", nullable = false)
    private MasterDataAaqilType aaqilType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false)
    private Genders gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ThirdPartyStatus status;

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