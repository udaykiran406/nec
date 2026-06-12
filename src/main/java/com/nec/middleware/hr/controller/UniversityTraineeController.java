package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.service.UniversityTraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(
        name = "University Trainee",
        description = "University Trainee Management APIs"
)
@RestController
@RequestMapping("/api/hr/universityTrainee")
@RequiredArgsConstructor
public class UniversityTraineeController {

    private final UniversityTraineeService universityTraineeService;

    // ------------------------------------------------------------------ CREATE

    @Operation(summary = "Create University Trainee")
    @PostMapping
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> createUniversityTrainee(
            @Valid @RequestBody UniversityTraineeRequestDto requestDto) {

        UniversityTraineeResponseDto universityTraineeResponse =
                universityTraineeService.createTrainee(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        UniversityTraineeConstants.TRAINEE_CREATED,
                        universityTraineeResponse
                ));
    }

    // ------------------------------------------------------------------ UPDATE

    @Operation(summary = "Update University Trainee")
    @PatchMapping("/{universityTraineeId}")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> updateUniversityTrainee(
            @PathVariable String universityTraineeId,
            @Valid @RequestBody UniversityTraineeRequestDto requestDto) {

        UniversityTraineeResponseDto response =
                universityTraineeService.updateTrainee(
                        universityTraineeId,
                        requestDto
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_UPDATED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ GET BY BUSINESS ID

    @Operation(summary = "Get University Trainee by University Trainee ID")
    @GetMapping("/{universityTraineeId}")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> getByUniversityTraineeId(
            @PathVariable String universityTraineeId) {

        UniversityTraineeResponseDto response =
                universityTraineeService.getTraineeByUniversityTraineeId(
                        universityTraineeId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ GET ALL

    @Operation(summary = "Get All University Trainees")
    @PostMapping("/getAllUniversityTrainees")
    public ResponseEntity<ApiResponse<Page<UniversityTraineeResponseDto>>> getAllUniversityTrainee(
            @RequestBody(required = false) UniversityTraineeFilterRequestDto request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UniversityTraineeResponseDto> response =
                universityTraineeService.getAllUniversityTrainees(
                        request,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_LIST_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ CHANGE STATUS

    @Operation(summary = "Change University Trainee Status")
    @PatchMapping("/{universityTraineeId}/status")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> changeUniversityTraineeStatus(
            @PathVariable String universityTraineeId,
            @RequestParam Boolean isActive) {

        UniversityTraineeResponseDto response =
                universityTraineeService.changeStatus(
                        universityTraineeId,
                        isActive
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_STATUS_CHANGED,
                        response
                )
        );
    }
}