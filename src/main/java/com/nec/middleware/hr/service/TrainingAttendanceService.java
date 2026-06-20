package com.nec.middleware.hr.service;

import com.nec.middleware.hr.dto.request.GetTrainingAttendanceRequestDto;

import com.nec.middleware.hr.dto.request.TrainingAttendanceFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingAttendanceRequestDtos;
import com.nec.middleware.hr.dto.response.SaveTrainingAttendanceResponseDto;
import com.nec.middleware.hr.dto.response.TrainingAttendanceGridResponseDto;
import com.nec.middleware.hr.dto.response.TrainingAttendanceResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.nec.middleware.hr.dto.request.UploadSignedAttendanceRequestDto;
import com.nec.middleware.hr.dto.response.SignedAttendanceResponseDto;

import java.time.LocalDate;

public interface TrainingAttendanceService {

    TrainingAttendanceResponseDto createAttendance(
            Long trainingClassId,
            LocalDate attendanceDate,
            String attendanceRecords,
            MultipartFile signedSheet);

    TrainingAttendanceGridResponseDto getAttendance(
            GetTrainingAttendanceRequestDto request);

    SaveTrainingAttendanceResponseDto saveAttendance(
            TrainingAttendanceRequestDtos request);

    Page<TrainingAttendanceResponseDto> getAllAttendance(
            TrainingAttendanceFilterRequestDto filterDto,
            int page,
            int size);

    TrainingAttendanceResponseDto changeStatus(
            Long id,
            Boolean isActive);

    SignedAttendanceResponseDto uploadSignedAttendanceSheet(
            UploadSignedAttendanceRequestDto request,
            MultipartFile signedSheet);
}