package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.TrainingClassConstants;
import com.nec.middleware.hr.dto.request.TrainingClassListRequestDto;
import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.service.TrainingClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(
        name = "Training Classes",
        description = "HR Training Class Management APIs"
)
@RestController
@RequestMapping("/api/training/classes")
@RequiredArgsConstructor
public class TrainingClassController {

    private final TrainingClassService trainingClassService;

    @Operation(summary = "Create Training Class")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TrainingClassResponse>> createTrainingClass(
            @Valid @RequestBody TrainingClassRequest request) {

        TrainingClassResponse response = trainingClassService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        TrainingClassConstants.CLASS_CREATED,
                        response
                ));
    }

    @Operation(summary = "Update Training Class by Class Code")
    @PutMapping("/update/{classCode}")
    public ResponseEntity<ApiResponse<TrainingClassResponse>> updateTrainingClass(
            @PathVariable String classCode,
            @Valid @RequestBody TrainingClassRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingClassConstants.CLASS_UPDATED,
                        trainingClassService.update(classCode, request)
                )
        );
    }

    @Operation(summary = "Get Training Class by Class Code")
    @GetMapping("/{classCode}")
    public ResponseEntity<ApiResponse<TrainingClassResponse>> getByClassCode(
            @PathVariable String classCode) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingClassConstants.CLASS_FETCHED,
                        trainingClassService.getByClassCode(classCode)
                )
        );
    }

    @Operation(summary = "Get Paginated & Filtered Training Class List")
    @PostMapping("/getAllTrainingClasses")
    public ResponseEntity<ApiResponse<Page<TrainingClassResponse>>> getAllTrainingClasses(
            @RequestBody(required = false) TrainingClassListRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingClassConstants.CLASS_LIST_FETCHED,
                        trainingClassService.getAllTrainingClasses(filterDto, page, size)
                )
        );
    }

    @Operation(summary = "Activate or Deactivate Training Class")
    @PatchMapping("/status/{classCode}")
    public ResponseEntity<ApiResponse<TrainingClassResponse>> updateStatus(
            @PathVariable String classCode,
            @RequestParam Boolean isActive) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        TrainingClassConstants.CLASS_STATUS_UPDATED,
                        trainingClassService.updateStatus(classCode, isActive)
                )
        );
    }
}