package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;

import java.util.List;

public interface TrainingClassService {

    TrainingClassResponse save(TrainingClassRequest request);

    TrainingClassResponse getById(Long id);

    List<TrainingClassResponse> getAll();

    void delete(Long id);
}