package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.PoliticalPartyAgentListRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import org.springframework.data.domain.Page;


public interface PoliticalPartyAgentService {

    PoliticalPartyAgentResponseDto saveOrUpdate(PoliticalPartyAgentRequestDto requestDto);

    PoliticalPartyAgentResponseDto getById(Long id);

    Page<PoliticalPartyAgentResponseDto> getAll(PoliticalPartyAgentListRequestDto filterDto);

    PoliticalPartyAgentResponseDto changeStatus(Long id);

    void softDelete(Long id);
}