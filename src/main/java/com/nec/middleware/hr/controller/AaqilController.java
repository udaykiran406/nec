package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.AaqilConstants;
import com.nec.middleware.hr.dto.request.AaqilListRequestDto;
import com.nec.middleware.hr.dto.request.AaqilRequestDto;
import com.nec.middleware.hr.dto.response.AaqilResponseDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
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

    private final AaqilService service;

    // ------------------------------------------------------------------ POST: Save / Update

    @Operation(summary = "Save or Update Aaqil")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> saveOrUpdate(
            @Valid @RequestBody AaqilRequestDto requestDto) {

        AaqilResponseDto response = service.saveOrUpdate(requestDto);

        boolean isCreate = requestDto.getId() == null;
        String message   = isCreate
                ? AaqilConstants.AAQIL_CREATED
                : AaqilConstants.AAQIL_UPDATED;

        return isCreate
                ? ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message, response))
                : ResponseEntity.ok(ApiResponse.success(message, response));
    }

    // ------------------------------------------------------------------ GET: By ID

    @Operation(summary = "Get Aaqil by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(AaqilConstants.AAQIL_FETCHED, service.getById(id)));
    }

    // ------------------------------------------------------------------ GET: List

    @Operation(summary = "Get Paginated & Filtered Aaqil List")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AaqilResponseDto>>> getAll(
            @ModelAttribute AaqilListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(AaqilConstants.AAQIL_LIST_FETCHED, service.getAll(filterDto)));
    }

    // ------------------------------------------------------------------ POST: Toggle Status

    @Operation(summary = "Toggle Aaqil Active Status")
    @PostMapping("/status/{id}")
    public ResponseEntity<ApiResponse<AaqilResponseDto>> changeStatus(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        AaqilConstants.AAQIL_STATUS_CHANGED,
                        service.changeStatus(id)));
    }

    // ------------------------------------------------------------------ POST: Soft Delete

    @Operation(summary = "Soft Delete Aaqil")
    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> softDelete(@PathVariable Long id) {

        service.softDelete(id);
        return ResponseEntity.ok(
                ApiResponse.success(AaqilConstants.AAQIL_DELETED, null));
    }
}

