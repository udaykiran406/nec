package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.MinistryofInteriorFilterRequestDto;
import com.nec.middleware.hr.dto.request.MinistryofInteriorRequestDto;
import com.nec.middleware.hr.dto.response.MinistryofInteriorResponseDto;
import org.springframework.data.domain.Page;

public interface MinistryofInteriorService {

    MinistryofInteriorResponseDto saveMinistryofInterior(MinistryofInteriorRequestDto ministryofInteriorRequestDto);

    MinistryofInteriorResponseDto getMinistryofInteriorById(String ministryofInteriorId);

    Page<MinistryofInteriorResponseDto> getAllMinistryofInterior(
            MinistryofInteriorFilterRequestDto filterDto, int page, int size);

    MinistryofInteriorResponseDto changeStatus(String ministryofInteriorId, Boolean isActiveFlag);

    MinistryofInteriorResponseDto updateMinistryofInterior(
            String ministryofInteriorId,
            MinistryofInteriorRequestDto ministryofInteriorRequestDto
    );
}