package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.workflow.dto.response.WorkflowInboxDto;

public interface TemporaryContractService {

    WorkflowInboxDto createTemporaryContract(TemporaryContractRequestDto requestDto);
}
