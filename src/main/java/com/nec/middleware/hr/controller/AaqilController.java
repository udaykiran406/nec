package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.AaqilConstants;
import com.nec.middleware.hr.constant.PoliticalPartyAgentConstants;
import com.nec.middleware.hr.dto.request.AaqilFilterRequestDto;
import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentFilterRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.service.AaqilService;
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
        name = "Aaqil",
        description = "Aaqil (Traditional Leader) Management APIs"
)
@RestController
@RequestMapping("/api/hr/aaqils")
@RequiredArgsConstructor
public class AaqilController {

    private final AaqilService aaqilservice;

    // ------------------------------------------------------------------ SAVE

    @Operation(summary = "Create Aaqil")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> createAaqil(
            @Valid @RequestBody AaqilRequestDto requestDto) {

        AaqilResponseDto response = aaqilservice.saveAaqil(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        AaqilConstants.AAQIL_CREATED,
                        response));
    }

    // ------------------------------------------------------------------ UPDATE

    @Operation(summary = "Update Aaqil")
    @PatchMapping("/{aaqilId}")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> updateAaqil(
            @PathVariable String aaqilId,
            @Valid @RequestBody AaqilRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        AaqilConstants.AAQIL_UPDATED,
                        aaqilservice.updateAaqil(
                                aaqilId,
                                requestDto)));
    }

    // ------------------------------------------------------------------ GET BY ID

    @Operation(summary = "Get Aaqil by Aaqil ID")
    @GetMapping("/{aaqilId}")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> getAaqilById(
            @PathVariable String aaqilId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        AaqilConstants.AAQIL_FETCHED,
                        aaqilservice.getAaqilById(aaqilId)
                )
        );
    }

    // ------------------------------------------------------------------ GET ALL

    @Operation(summary = "Get Paginated & Filtered Aaqil List")
    @PostMapping("/getAllAaqils")
    public ResponseEntity<ApiResponse<Page<AaqilResponseDto>>> getAllAaqils(
            @RequestBody(required = false) AaqilFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<AaqilResponseDto> response =
                aaqilservice.getAllAaqils(
                        filterDto,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                       AaqilConstants.AAQIL_LIST_FETCHED,
                        response
                ));
    }
    // ------------------------------------------------------------------ CHANGE STATUS
    @Operation(summary = "Change Aaqil Status")
    @PatchMapping("/status/{aaqilId}")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> changeStatus(
            @PathVariable String aaqilId,
            @RequestParam Boolean isActive) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        AaqilConstants.AAQIL_STATUS_CHANGED,
                        aaqilservice.changeStatus(
                                aaqilId,
                                isActive)));
    }
}