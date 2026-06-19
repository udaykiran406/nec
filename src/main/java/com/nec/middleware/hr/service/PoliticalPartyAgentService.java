package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.PoliticalPartyAgentFilterRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface PoliticalPartyAgentService {

    PoliticalPartyAgentResponseDto savePartyAgent(PoliticalPartyAgentRequestDto requestDto, MultipartFile photo);

    PoliticalPartyAgentResponseDto getPolticalPartyAgentById(String partyAgentUserId);

    Page<PoliticalPartyAgentResponseDto> getAllPartyAgents(PoliticalPartyAgentFilterRequestDto filterDto, int page, int size);

    PoliticalPartyAgentResponseDto changeStatus(String partyAgentUserId, Boolean isActiveFlag);
    PoliticalPartyAgentResponseDto updatePoliticalPartyAgent(String partyAgentUserId,PoliticalPartyAgentRequestDto requestDto, MultipartFile photo);

}