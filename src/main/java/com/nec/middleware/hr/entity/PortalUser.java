package com.nec.middleware.portal.entity;

import com.nec.middleware.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Maps to: nec_portal_users
 * Audit fields (isActive, isDeleted, createdBy, createdAt, updatedBy, updatedAt)
 * are inherited from {@link AuditableEntity}.
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

    @Column(name = "user_name", nullable = false, length = 150)
    private String userName;

    @Column(name = "gender_id", nullable = false)
    private Long genderId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(name = "faculty", length = 100)
    private String faculty;

    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "portal_user_type_id")
    private Long portalUserTypeId;

    @Column(name = "reference_id")
    private Long referenceId;
}
