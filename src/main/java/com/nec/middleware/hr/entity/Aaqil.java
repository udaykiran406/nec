package com.nec.middleware.hr.entity;


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

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "aaqil_type_id", nullable = false)
    private Long aaqilTypeId;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "gender_id", nullable = false)
    private Long genderId;

    @Column(name = "age")
    private Short age;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "status_id", nullable = false)
    private Long statusId;
}
