package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.Lookups.entity.PaymentMethods;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.entity.MasterDataRegion;
import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: nec_hr_university_trainees
 *
 * universityTraineeId  → business key, auto-generated (e.g. UT001, UT002 …)
 *                         mirrors portalUserId in PortalUser.
 *
 * All FK columns store raw Long ids; name resolution is done in the
 * service layer by querying the respective lookup / master-data repositories.
 *
 * Audit fields (isActive, isDeleted, createdBy, createdAt, updatedBy, updatedAt)
 * are inherited from {@link AuditableEntity}.
 */
@Entity
@Table(name = "nec_hr_university_trainees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityTrainee extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key — generated once at creation, never changed. e.g. UT001 */
    @Column(name = "university_trainee_id", nullable = false, unique = true, length = 20)
    private String universityTraineeId;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "age", nullable = false)
    private Short age;

    @Column(name = "phone", nullable = false, unique = true,length = 30)
    private String phone;

    @Column(name = "email", nullable = false, unique = true,length = 100)
    private String email;

    @Column(name = "semester", nullable = false, length = 50)
    private String semester;

    @Column(name = "faculty", nullable = false, length = 100)
    private String faculty;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "status", nullable = false)
    private String status;
    // ------------------------------------------------------------------ LOOKUPS FKs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false)
    private Genders gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethods paymentMethod;

// ------------------------------------------------------------------ MASTER DATA FKs

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private MasterDataUniversity university;

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