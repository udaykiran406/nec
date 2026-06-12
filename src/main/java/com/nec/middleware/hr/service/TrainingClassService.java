package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TrainingClassListRequestDto;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TrainingClassService {

    TrainingClassResponse create(TrainingClassRequest request);

    TrainingClassResponse update(String classCode, TrainingClassRequest request);

    TrainingClassResponse getByClassCode(String classCode);

    Page<TrainingClassResponse> getAllTrainingClasses(TrainingClassListRequestDto filterDto, int pageNumber, int pageSize);

    TrainingClassResponse updateStatus(String classCode, Boolean isActive);
}