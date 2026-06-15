package com.nec.middleware.hr.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliticalPartyAgentResponseDto {

    private String politicalPartyAgentUserId;
    private IdValueDto politicalPartyName;
    private String agentName;
    private IdValueDto gender;
    private String phone;
    private String email;
    private String photoUrl;
    private IdValueDto pollingStation;
    private IdValueDto region;
    private IdValueDto district;
    private IdValueDto city;
    private IdValueDto status;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
