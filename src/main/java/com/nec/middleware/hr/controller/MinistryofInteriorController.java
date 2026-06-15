package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.MinistryofInteriorConstants;
import com.nec.middleware.hr.dto.request.MinistryofInteriorFilterRequestDto;
import com.nec.middleware.hr.dto.request.MinistryofInteriorRequestDto;
import com.nec.middleware.hr.dto.response.MinistryofInteriorResponseDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.service.MinistryofInteriorService;
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
        name = "Ministry of Interior",
        description = "Ministry of Interior Management APIs"
)
@RestController
@RequestMapping("/api/hr/ministry-of-interior")
@RequiredArgsConstructor
public class MinistryofInteriorController {

    private final MinistryofInteriorService ministryofInteriorService;

    // ------------------------------------------------------------------ SAVE

    @Operation(summary = "Create Ministry of Interior")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> createMinistryofInterior(
            @Valid @RequestBody MinistryofInteriorRequestDto requestDto) {

        MinistryofInteriorResponseDto response = ministryofInteriorService.saveMinistryofInterior(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_CREATED,
                        response));
    }

    // ------------------------------------------------------------------ UPDATE

    @Operation(summary = "Update Ministry of Interior")
    @PatchMapping("/{ministryofInteriorId}")
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> updateMinistryofInterior(
            @PathVariable String ministryofInteriorId,
            @Valid @RequestBody MinistryofInteriorRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_UPDATED,
                        ministryofInteriorService.updateMinistryofInterior(
                                ministryofInteriorId,
                                requestDto)));
    }

    // ------------------------------------------------------------------ GET BY ID

    @Operation(summary = "Get Ministry of Interior by ID")
    @GetMapping("/{ministryofInteriorId}")
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> getMinistryofInteriorById(
            @PathVariable String ministryofInteriorId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_FETCHED,
                        ministryofInteriorService.getMinistryofInteriorById(ministryofInteriorId)
                )
        );
    }

    // ------------------------------------------------------------------ GET ALL

    @Operation(summary = "Get Paginated & Filtered Ministry of Interior List")
    @PostMapping("/getAll")
    public ResponseEntity<ApiResponse<Page<MinistryofInteriorResponseDto>>> getAllMinistryofInterior(
            @RequestBody(required = false) MinistryofInteriorFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MinistryofInteriorResponseDto> response =
                ministryofInteriorService.getAllMinistryofInterior(
                        filterDto,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_LIST_FETCHED,
                        response
                ));
    }
    // ------------------------------------------------------------------ CHANGE STATUS
    @Operation(summary = "Change Ministry of Interior Status")
    @PatchMapping("/status/{ministryofInteriorId}")
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> changeStatus(
            @PathVariable String ministryofInteriorId,
            @RequestParam Boolean isActive) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_STATUS_CHANGED,
                        ministryofInteriorService.changeStatus(
                                ministryofInteriorId,
                                isActive)));
    }
}
