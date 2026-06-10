package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.portal.dto.request.PortalUserRequestDto;
import com.nec.middleware.portal.dto.response.PortalUserResponseDto;
import org.springframework.data.domain.Page;

public interface PortalUserService {

    PortalUserResponseDto saveOrUpdate(PortalUserRequestDto requestDto);

    PortalUserResponseDto getById(Long id);

    Page<PortalUserResponseDto> getAll(PortalUserListRequestDto filterDto);

    PortalUserResponseDto changeStatus(Long id);

    void softDelete(Long id);
}
