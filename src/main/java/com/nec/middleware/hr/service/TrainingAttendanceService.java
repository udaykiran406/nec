package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.TrainingAttendanceFilterRequestDto;
import com.nec.middleware.hr.dto.response.TrainingAttendanceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface TrainingAttendanceService {

    TrainingAttendanceResponseDto createAttendance(
            Long trainingClassId,
            LocalDate attendanceDate,
            String attendanceRecords,
            MultipartFile signedSheet);

    Page<TrainingAttendanceResponseDto> getAllAttendance(
            TrainingAttendanceFilterRequestDto filterDto,
            int page,
            int size);

    TrainingAttendanceResponseDto changeStatus(
            Long id,
            Boolean isActive);
}