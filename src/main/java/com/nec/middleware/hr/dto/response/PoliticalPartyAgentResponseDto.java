package com.nec.middleware.hr.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliticalPartyAgentResponseDto {

    private Long          id;
    private String        code;
    private Long          politicalPartyNameId;
    private String        agentName;
    private Long          genderId;
    private String        phone;
    private String        email;
    private String        photoUrl;
    private Long          pollingStationId;
    private Long          regionId;
    private Long          districtId;
    private Long          cityId;
    private Long          statusId;
    private Boolean       isActive;
    private Boolean       isDeleted;
    private Long          createdBy;
    private LocalDateTime createdAt;
    private Long          updatedBy;
    private LocalDateTime updatedAt;
}
