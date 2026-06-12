package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;

import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import org.springframework.data.domain.Page;

public interface PortalUserService {

    PortalUserResponseDto createPortalUser(PortalUserRequestDto requestDto);

    PortalUserResponseDto getUserByPortalUserId(String portalUserId);

    Page<PortalUserResponseDto> getAllPortalUsers(PortalUserListRequestDto filterDto, int pageNumber,int pageSize);

    PortalUserResponseDto softDelete(String portalUserId);

    PortalUserResponseDto updatePortalUser(String portalUserId, PortalUserRequestDto request);
}
