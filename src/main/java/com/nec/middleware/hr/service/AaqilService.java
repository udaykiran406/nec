package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.AaqilFilterRequestDto;
import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import org.springframework.data.domain.Page;

public interface AaqilService {

    AaqilResponseDto saveAaqil(AaqilRequestDto aaqilRequestDto);

    AaqilResponseDto getAaqilById(String aaqilCode);

    Page<AaqilResponseDto> getAllAaqils(
            AaqilFilterRequestDto filterDto,
            int page,
            int size
    );

    AaqilResponseDto changeStatus(String aaqilCode, Boolean isActiveFlag);

    AaqilResponseDto updateAaqil(
            String aaqilCode,
            AaqilRequestDto aaqilRequestDto
    );
}