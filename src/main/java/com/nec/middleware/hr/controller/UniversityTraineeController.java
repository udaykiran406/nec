package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeListRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
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
@RequestMapping("/api/hr/university-trainees")
@RequiredArgsConstructor
public class UniversityTraineeController {

    private final UniversityTraineeService service;

    // ------------------------------------------------------------------ POST: Create

    /**
     * POST /api/hr/university-trainees
     * Creates a new university trainee record.
     * send id for update
     */
    @Operation(
            summary = "Save or Update University Trainee"
    )
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> saveOrUpdate(
            @Valid @RequestBody UniversityTraineeRequestDto requestDto) {

        UniversityTraineeResponseDto response =
                service.saveOrUpdate(requestDto);

        boolean isCreate = requestDto.getId() == null;
        String message   = isCreate
                ? UniversityTraineeConstants.TRAINEE_CREATED
                : UniversityTraineeConstants.TRAINEE_UPDATED;

        return isCreate
                ? ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message, response))
                : ResponseEntity.ok(ApiResponse.success(message, response));
    }

    // ------------------------------------------------------------------ GET: By ID

    /**
     * GET /api/hr/university-trainees/{id}
     * Fetches a single trainee by id (excludes soft-deleted).
     */
    @Operation(summary = "Get University Trainee by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> getById(@PathVariable Long id) {
        UniversityTraineeResponseDto response = service.getById(id);
        return ResponseEntity.ok(ApiResponse.success(UniversityTraineeConstants.TRAINEE_FETCHED, response));
    }

    // ------------------------------------------------------------------ GET: List

    /**
     * GET /api/hr/university-trainees
     * Paginated + filtered list. All query params optional.
     * Params: universityId, regionId, districtId, cityId, statusId, isActive, page, size
     */
    @Operation(summary = "Get Paginated & Filtered University Trainee List")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UniversityTraineeResponseDto>>> getAll(
            @ModelAttribute UniversityTraineeListRequestDto filterDto) {

        Page<UniversityTraineeResponseDto> page = service.getAll(filterDto);
        return ResponseEntity.ok(ApiResponse.success(UniversityTraineeConstants.TRAINEE_LIST_FETCHED, page));
    }

    // ------------------------------------------------------------------ POST: Status Change (soft-delete)

    /**
     * POST /api/hr/university-trainees/status/{id}
     * Changes the active flag.
     * If isActive = false → record is soft-deleted (is_deleted = true).
     */
    @Operation(summary = "Toggle University Trainee Active Status")
    @PostMapping("/status/{id}")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> changeStatus(
            @PathVariable Long id) {

        UniversityTraineeResponseDto response = service.changeStatus(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_STATUS_CHANGED,
                        response));
    }

    @Operation(summary = "Soft Delete University Trainee")
    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> softDelete(
            @PathVariable Long id) {

        service.softDelete(id);

        return ResponseEntity.ok(
                ApiResponse.success(UniversityTraineeConstants.TRAINEE_DELETED,null)
        );
    }
}