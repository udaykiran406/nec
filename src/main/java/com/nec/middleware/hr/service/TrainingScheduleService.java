package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TrainingScheduleFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingScheduleRequestDto;
import com.nec.middleware.hr.dto.response.TrainingScheduleResponseDto;
import org.springframework.data.domain.Page;

public interface TrainingScheduleService {

    TrainingScheduleResponseDto createSchedule(
            TrainingScheduleRequestDto requestDto);

    TrainingScheduleResponseDto getScheduleByScheduleCode(
            String scheduleCode);

    Page<TrainingScheduleResponseDto> getAllSchedules(
            TrainingScheduleFilterRequestDto filterDto,
            int pageNumber,
            int pageSize);

    TrainingScheduleResponseDto changeStatus(
            String scheduleCode,
            Boolean isActive);

    TrainingScheduleResponseDto updateSchedule(
            String scheduleCode,
            TrainingScheduleRequestDto requestDto);
}