package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.TrainingTraineeAllocationConstants;
import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingTraineeAllocationRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.TrainingTraineeAllocationResponseDto;
import com.nec.middleware.hr.service.TrainingTraineeAllocationService;
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
        name = "Training Trainee Allocation",
        description = "Training Trainee Allocation Management APIs"
)
@RestController
@RequestMapping("/api/hr/trainingTraineeAllocation")
@RequiredArgsConstructor
public class TrainingTraineeAllocationController {

    private final TrainingTraineeAllocationService trainingTraineeAllocationService;

    // ------------------------------------------------------------------ CREATE

    @Operation(summary = "Create Training Trainee Allocation")
    @PostMapping
    public ResponseEntity<ApiResponse<TrainingTraineeAllocationResponseDto>> createAllocation(
            @Valid @RequestBody TrainingTraineeAllocationRequestDto requestDto) {

        TrainingTraineeAllocationResponseDto response =
                trainingTraineeAllocationService.createAllocation(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        TrainingTraineeAllocationConstants.ALLOCATION_CREATED,
                        response
                ));
    }

    // ------------------------------------------------------------------ UPDATE

    @Operation(summary = "Update Training Trainee Allocation")
    @PatchMapping("/{allocationCode}")
    public ResponseEntity<ApiResponse<TrainingTraineeAllocationResponseDto>> updateAllocation(
            @PathVariable String allocationCode,
            @Valid @RequestBody TrainingTraineeAllocationRequestDto requestDto) {

        TrainingTraineeAllocationResponseDto response =
                trainingTraineeAllocationService.updateAllocation(
                        allocationCode,
                        requestDto
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingTraineeAllocationConstants.ALLOCATION_UPDATED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ GET BY BUSINESS CODE

    @Operation(summary = "Get Training Trainee Allocation by Allocation Code")
    @GetMapping("/{allocationCode}")
    public ResponseEntity<ApiResponse<TrainingTraineeAllocationResponseDto>> getByAllocationCode(
            @PathVariable String allocationCode) {

        TrainingTraineeAllocationResponseDto response =
                trainingTraineeAllocationService.getAllocationByAllocationCode(
                        allocationCode
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingTraineeAllocationConstants.ALLOCATION_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ GET ALL

    @Operation(summary = "Get All Training Trainee Allocations")
    @PostMapping("/getAllTrainingTraineeAllocations")
    public ResponseEntity<ApiResponse<Page<TrainingTraineeAllocationResponseDto>>> getAllAllocations(
            @RequestBody(required = false) TrainingTraineeAllocationFilterRequestDto request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TrainingTraineeAllocationResponseDto> response =
                trainingTraineeAllocationService.getAllAllocations(
                        request,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingTraineeAllocationConstants.ALLOCATION_LIST_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ CHANGE STATUS

    @Operation(summary = "Change Training Trainee Allocation Status")
    @PatchMapping("/{allocationCode}/status")
    public ResponseEntity<ApiResponse<TrainingTraineeAllocationResponseDto>> changeAllocationStatus(
            @PathVariable String allocationCode,
            @RequestParam Boolean isActive) {

        TrainingTraineeAllocationResponseDto response =
                trainingTraineeAllocationService.changeStatus(
                        allocationCode,
                        isActive
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingTraineeAllocationConstants.ALLOCATION_STATUS_CHANGED,
                        response
                )
        );
    }
}