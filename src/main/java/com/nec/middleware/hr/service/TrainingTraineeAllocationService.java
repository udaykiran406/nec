package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationRequestDto;
import com.nec.middleware.hr.dto.response.TrainingTraineeAllocationResponseDto;
import org.springframework.data.domain.Page;

public interface TrainingTraineeAllocationService {

    TrainingTraineeAllocationResponseDto createAllocation(
            TrainingTraineeAllocationRequestDto requestDto);

    TrainingTraineeAllocationResponseDto getAllocationByAllocationCode(
            String allocationCode);

    Page<TrainingTraineeAllocationResponseDto> getAllAllocations(
            TrainingTraineeAllocationFilterRequestDto filterDto,
            int pageNumber,
            int pageSize);

    TrainingTraineeAllocationResponseDto changeStatus(
            String allocationCode,
            Boolean isActive);

    TrainingTraineeAllocationResponseDto updateAllocation(
            String allocationCode,
            TrainingTraineeAllocationRequestDto requestDto);
}