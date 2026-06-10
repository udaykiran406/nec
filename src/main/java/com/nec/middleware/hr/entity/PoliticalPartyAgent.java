package com.nec.middleware.hr.entity;

import com.nec.middleware.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nec_hr_political_party_agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliticalPartyAgent extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "political_party_name_id", nullable = false)
    private Long politicalPartyNameId;

    @Column(name = "agent_name", nullable = false, length = 120)
    private String agentName;

    @Column(name = "gender_id", nullable = false)
    private Long genderId;

    @Column(name = "phone", nullable = false, length = 30)
    private String phone;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "polling_station_id", nullable = false)
    private Long pollingStationId;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(name = "status_id", nullable = false)
    private Long statusId;
}
