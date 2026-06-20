package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.PortalUserFilterRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
public interface PortalUserService {
    PortalUserResponseDto createPortalUser(PortalUserRequestDto requestDto, MultipartFile photo);

    PortalUserResponseDto getUserByPortalUserId(String portalUserId);

    Page<PortalUserResponseDto> getAllPortalUsers(PortalUserFilterRequestDto filterDto, int pageNumber, int pageSize);

    PortalUserResponseDto changeStatus(String portalUserId,Boolean isActive);

    PortalUserResponseDto updatePortalUser(String portalUserId, PortalUserRequestDto request,MultipartFile photo);
}
