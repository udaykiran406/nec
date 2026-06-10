package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.AaqilListRequestDto;
import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import org.springframework.data.domain.Page;

public interface AaqilService {

    AaqilResponseDto saveOrUpdate(AaqilRequestDto requestDto);

    AaqilResponseDto getById(Long id);

    Page<AaqilResponseDto> getAll(AaqilListRequestDto filterDto);

    AaqilResponseDto changeStatus(Long id);

    void softDelete(Long id);
}
