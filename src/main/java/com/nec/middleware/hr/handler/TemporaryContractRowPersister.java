package com.nec.middleware.hr.handler;

import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.service.impl.TemporaryContractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemporaryContractRowPersister {

    private final TemporaryContractServiceImpl service;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TemporaryContractResponseDto persistSingleRow(TemporaryContractRequestDto dto) {
        log.debug("Persisting temporary contract row");
        return service.createOrUpdateContract(dto);
    }
}