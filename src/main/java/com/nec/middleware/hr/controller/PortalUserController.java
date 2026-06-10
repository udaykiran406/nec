package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.PortalUserConstants;

import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;

import com.nec.middleware.hr.service.PortalUserService;
import com.nec.middleware.portal.dto.request.PortalUserRequestDto;
import com.nec.middleware.portal.dto.response.PortalUserResponseDto;

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
        name = "Portal Users",
        description = "Portal User Management APIs"
)
@RestController
@RequestMapping("/api/portal/users")
@RequiredArgsConstructor
public class PortalUserController {

    private final PortalUserService service;

    // ------------------------------------------------------------------ POST: Save / Update

    @Operation(summary = "Save or Update Portal User")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> saveOrUpdate(
            @Valid @RequestBody PortalUserRequestDto requestDto) {

        PortalUserResponseDto response = service.saveOrUpdate(requestDto);

        boolean isCreate = requestDto.getId() == null;
        String message   = isCreate
                ? PortalUserConstants.USER_CREATED
                : PortalUserConstants.USER_UPDATED;

        return isCreate
                ? ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message, response))
                : ResponseEntity.ok(ApiResponse.success(message, response));
    }

    // ------------------------------------------------------------------ GET: By ID

    @Operation(summary = "Get Portal User by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(PortalUserConstants.USER_FETCHED, service.getById(id)));
    }

    // ------------------------------------------------------------------ GET: List

    @Operation(summary = "Get Paginated & Filtered Portal User List")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PortalUserResponseDto>>> getAll(
            @ModelAttribute PortalUserListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(PortalUserConstants.USER_LIST_FETCHED, service.getAll(filterDto)));
    }

    // ------------------------------------------------------------------ POST: Toggle Status

    @Operation(summary = "Toggle Portal User Active Status")
    @PostMapping("/status/{id}")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> changeStatus(@PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_STATUS_CHANGED,
                        service.changeStatus(id)));
    }

    // ------------------------------------------------------------------ POST: Soft Delete

    @Operation(summary = "Soft Delete Portal User")
    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> softDelete(@PathVariable Long id) {

        service.softDelete(id);
        return ResponseEntity.ok(
                ApiResponse.success(PortalUserConstants.USER_DELETED, null));
    }
}
