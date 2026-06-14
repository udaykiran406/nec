package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TrainingClassListRequestDto;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import org.springframework.data.domain.Page;

public interface TrainingClassService {

    TrainingClassResponse createTrainingClass(TrainingClassRequest request);

    TrainingClassResponse updateTrainingClass(String classCode, TrainingClassRequest request);

    TrainingClassResponse getTrainingClassByClassCode(String classCode);

    Page<TrainingClassResponse> getAllTrainingClasses(TrainingClassListRequestDto filterDto, int pageNumber, int pageSize);

    TrainingClassResponse updateTrainingClassStatus(String classCode, Boolean isActive);
}