package com.nec.middleware.hr.entity;


import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.Lookups.entity.ThirdPartyStatus;
import com.nec.middleware.masterdata.entity.*;
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

    @Column(name = "political_party_user_id", nullable = false, unique = true, length = 20)
    private String politicalPartyAgentUserId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "political_party_name_id", nullable = false)
    private MasterDataPoliticalParty politicalPartyName;

    @Column(name = "agent_name", nullable = false, length = 120)
    private String agentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = false)
    private Genders gender;

    @Column(name = "phone", nullable = false, unique = true,length = 30)
    private String phone;

    @Column(name = "email",unique = true ,nullable = false, length = 100)
    private String email;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polling_station_id", nullable = false)
    private MasterDataPollingStation pollingStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private MasterDataRegion region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private MasterDataDistrict district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private MasterDataCity city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ThirdPartyStatus status;
}
