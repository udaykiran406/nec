package com.nec.middleware.hr.entity;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "gender_id", nullable = false)
    private Long genderId;

    @Column(name = "age", nullable = false)
    private Short age;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "payment_method_id", nullable = false)
    private Long paymentMethodId;

    @Column(name = "university_id", nullable = false)
    private Long universityId;

    @Column(name = "semester", nullable = false, length = 50)
    private String semester;

    @Column(name = "faculty", nullable = false, length = 100)
    private String faculty;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "status_id", nullable = false)
    private Long statusId;
}
