package com.nec.middleware.hr.service;


import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.BulkUploadResultDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;


public interface UniversityTraineeService {
    UniversityTraineeResponseDto createTrainee(UniversityTraineeRequestDto requestDto,MultipartFile photo);


    // ------------------------------------------------------------------ Bulk create
    /**
     * Parse {@code file} (Excel .xlsx or CSV) and persist every valid row as a
     * new {@link com.nec.middleware.hr.entity.UniversityTrainee}.
     *
     * <p>Rows that fail validation or FK resolution are collected in the
     * returned {@link BulkUploadResultDto#getErrors()} list rather than
     * rolling back the entire batch — i.e. partial success is intentional.
     * Each {@code RowErrorDto} retains the row's original column values
     * ({@code rawData}) so the controller can regenerate a downloadable
     * error-report file when {@code failureCount > 0}.
     *
     * <p>Photos are not supported in bulk mode; {@code photoPath} is left
     * {@code null} for all bulk-created records.
     *
     * @param file Excel (.xlsx) or CSV file whose first row is a header
     * @return result containing saved records and per-row errors (with raw data)
     */
    BulkUploadResultDto<UniversityTraineeResponseDto> bulkCreate(MultipartFile file);

    UniversityTraineeResponseDto getTraineeByUniversityTraineeId(String universityTraineeId);

    Page<UniversityTraineeResponseDto> getAllUniversityTrainees(UniversityTraineeFilterRequestDto filterDto, int pageNumber, int pageSize);

    UniversityTraineeResponseDto changeStatus(String universityTraineeId,Boolean isActive);

    UniversityTraineeResponseDto updateTrainee(String universityTraineeId, UniversityTraineeRequestDto request, MultipartFile photo);
}