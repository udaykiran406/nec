package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.PortalUserConstants;

import com.nec.middleware.hr.dto.request.PortalUserListRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;

import com.nec.middleware.hr.service.PortalUserService;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;

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
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalUserController {

    private final PortalUserService  portalUserService;

    // ------------------------------------------------------------------ POST: Create

    @Operation(summary = "Create Portal User")
    @PostMapping("/saveUser")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> savePortalUser(
            @Valid @RequestBody PortalUserRequestDto requestDto) {

        PortalUserResponseDto response = portalUserService.createPortalUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(PortalUserConstants.USER_CREATED, response));
    }

    // ------------------------------------------------------------------ GET: By portalUserId

    @Operation(summary = "Get Portal User by Portal User ID")
    @GetMapping("/{portalUserId}")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> getByPortalUserId(
            @PathVariable String portalUserId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_FETCHED,
                        portalUserService.getUserByPortalUserId(portalUserId)));
    }

    // ------------------------------------------------------------------ GET: List

    @Operation(summary = "Get Paginated & Filtered Portal User List")
    @GetMapping("/getAllPortalUsers")
    public ResponseEntity<ApiResponse<Page<PortalUserResponseDto>>> getAllPortalUsers(
            @ModelAttribute PortalUserListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_LIST_FETCHED,
                        portalUserService.getAllPortalUsers(filterDto)));
    }

    // ------------------------------------------------------------------ POST: Update

    @Operation(summary = "Update Portal User")
    @PostMapping("/update/{portalUserId}")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> updatePortalUser(
            @PathVariable String portalUserId,
            @Valid @RequestBody PortalUserRequestDto requestDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_UPDATED,
                        portalUserService.updatePortalUser(portalUserId, requestDto)));
    }

    // ------------------------------------------------------------------ PATCH: Soft Delete

    @Operation(summary = "Soft Delete Portal User")
    @PatchMapping("/delete/{portalUserId}")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> softDelete(
            @PathVariable String portalUserId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_DELETED,
                        portalUserService.softDelete(portalUserId)));
    }
}

