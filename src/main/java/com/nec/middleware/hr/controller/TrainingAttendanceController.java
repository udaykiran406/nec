package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.TrainingAttendanceConstants;
import com.nec.middleware.hr.dto.request.TrainingAttendanceFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingAttendanceRequestDtos;
import com.nec.middleware.hr.dto.request.UploadSignedAttendanceRequestDto;
import com.nec.middleware.hr.dto.response.*;
import com.nec.middleware.hr.service.TrainingAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import com.nec.middleware.hr.dto.request.GetTrainingAttendanceRequestDto;


import java.time.LocalDate;

@Slf4j
@Tag(
        name = "Training Attendance",
        description = "HR Training Attendance Management APIs"
)
@RestController
@RequestMapping("/api/training/attendance")
@RequiredArgsConstructor
public class TrainingAttendanceController {

    private final TrainingAttendanceService trainingAttendanceService;

    @Operation(summary = "Create Training Attendance")
    @PostMapping(
            value = "/createAttendance",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<TrainingAttendanceResponseDto>> createAttendance(
            @RequestParam Long trainingClassId,
            @RequestParam LocalDate attendanceDate,
            @RequestParam String attendanceRecords,
            @RequestPart(required = false) MultipartFile signedSheet) {

        TrainingAttendanceResponseDto response =
                trainingAttendanceService.createAttendance(
                        trainingClassId,
                        attendanceDate,
                        attendanceRecords,
                        signedSheet
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        TrainingAttendanceConstants.ATTENDANCE_CREATED,
                        response
                ));
    }

    @Operation(summary = "Get Paginated & Filtered Training Attendance List")
    @PostMapping("/getAllTrainingAttendance")
    public ResponseEntity<ApiResponse<Page<TrainingAttendanceResponseDto>>> getAllAttendance(
            @RequestBody(required = false) TrainingAttendanceFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingAttendanceConstants.ATTENDANCE_LIST_FETCHED,
                        trainingAttendanceService.getAllAttendance(filterDto, page, size)
                )
        );
    }

    @Operation(summary = "Activate or Deactivate Training Attendance")
    @PatchMapping("/changeAttendenceStatus/{id}")
    public ResponseEntity<ApiResponse<TrainingAttendanceResponseDto>> changeStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingAttendanceConstants.ATTENDANCE_STATUS_UPDATED,
                        trainingAttendanceService.changeStatus(id, isActive)
                )
        );
    }
    @Operation(summary = "Get Training Attendance")
    @PostMapping
    public ResponseEntity<ApiResponse<TrainingAttendanceGridResponseDto>> getAttendance(
            @Valid @RequestBody GetTrainingAttendanceRequestDto request) {

        TrainingAttendanceGridResponseDto response =
                trainingAttendanceService.getAttendance(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance fetched successfully",
                        response
                )
        );
    }

    @Operation(summary = "Save Training Attendance")
    @PostMapping("/saveAttendance")
    public ResponseEntity<ApiResponse<SaveTrainingAttendanceResponseDto>> saveAttendance(
            @Valid @RequestBody TrainingAttendanceRequestDtos request) {

        SaveTrainingAttendanceResponseDto response =
                trainingAttendanceService.saveAttendance(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Attendance saved successfully",
                        response
                )
        );
    }

    @PostMapping(value = "/upload-signed-sheet",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SignedAttendanceResponseDto>> uploadSignedAttendanceSheet(
            @RequestPart("request") UploadSignedAttendanceRequestDto request,
            @RequestPart("signedSheet") MultipartFile signedSheet) {

        SignedAttendanceResponseDto response =
                trainingAttendanceService.uploadSignedAttendanceSheet(
                        request,
                        signedSheet
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        "Signed attendance sheet uploaded successfully",
                        response
                ));
    }
}