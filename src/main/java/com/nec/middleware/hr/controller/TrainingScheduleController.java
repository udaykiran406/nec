package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.TrainingScheduleConstants;
import com.nec.middleware.hr.dto.request.TrainingScheduleFilterRequestDto;
import com.nec.middleware.hr.dto.request.TrainingScheduleRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.TrainingScheduleResponseDto;
import com.nec.middleware.hr.service.TrainingScheduleService;
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
        name = "Training Schedule",
        description = "HR Training Schedule Management APIs"
)
@RestController
@RequestMapping("/api/training/schedules")
@RequiredArgsConstructor
public class TrainingScheduleController {

    private final TrainingScheduleService trainingScheduleService;

    @Operation(summary = "Create Training Schedule")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TrainingScheduleResponseDto>> createSchedule(
            @Valid @RequestBody TrainingScheduleRequestDto request) {

        TrainingScheduleResponseDto response =
                trainingScheduleService.createSchedule(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        TrainingScheduleConstants.SCHEDULE_CREATED,
                        response
                ));
    }

    @Operation(summary = "Update Training Schedule")
    @PutMapping("/update/{scheduleCode}")
    public ResponseEntity<ApiResponse<TrainingScheduleResponseDto>> updateSchedule(
            @PathVariable String scheduleCode,
            @Valid @RequestBody TrainingScheduleRequestDto request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingScheduleConstants.SCHEDULE_UPDATED,
                        trainingScheduleService.updateSchedule(
                                scheduleCode,
                                request
                        )
                )
        );
    }

    @Operation(summary = "Get Training Schedule By Schedule Code")
    @GetMapping("/{scheduleCode}")
    public ResponseEntity<ApiResponse<TrainingScheduleResponseDto>> getScheduleByCode(
            @PathVariable String scheduleCode) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingScheduleConstants.SCHEDULE_FETCHED,
                        trainingScheduleService.getScheduleByScheduleCode(
                                scheduleCode
                        )
                )
        );
    }

    @Operation(summary = "Get All Training Schedules")
    @PostMapping("/getAllTrainingSchedules")
    public ResponseEntity<ApiResponse<Page<TrainingScheduleResponseDto>>> getAllSchedules(
            @RequestBody(required = false)
            TrainingScheduleFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingScheduleConstants.SCHEDULE_LIST_FETCHED,
                        trainingScheduleService.getAllSchedules(
                                filterDto,
                                page,
                                size
                        )
                )
        );
    }

    @Operation(summary = "Activate or Deactivate Training Schedule")
    @PatchMapping("/status/{scheduleCode}")
    public ResponseEntity<ApiResponse<TrainingScheduleResponseDto>> updateScheduleStatus(
            @PathVariable String scheduleCode,
            @RequestParam Boolean isActive) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingScheduleConstants.SCHEDULE_STATUS_CHANGED,
                        trainingScheduleService.changeStatus(
                                scheduleCode,
                                isActive
                        )
                )
        );
    }
}