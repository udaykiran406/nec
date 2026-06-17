package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;

public interface TemporaryContractService {

    TemporaryContractResponseDto createOrUpdateContract(TemporaryContractRequestDto requestDto);

}
