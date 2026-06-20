package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.PortalUserConstants;

import com.nec.middleware.hr.dto.request.PortalUserFilterRequestDto;
import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;

import com.nec.middleware.hr.service.PortalUserService;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;

import com.nec.middleware.rbacAuth.auth.utils.Authorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(
        name = "Portal Users",
        description = "Portal User Management APIs"
)
@RestController
@RequestMapping("/api/v1/hr/portalUser")
@RequiredArgsConstructor
public class PortalUserController {

    private final PortalUserService  portalUserService;

    // ------------------------------------------------------------------ POST: Create

    @Operation(summary = "Create Portal User",
            description = "multipart/form-data: 'requestDto' part is the JSON payload, 'photo' part is the image file (jpg/jpeg/png/webp, max 5MB).")
    @Authorize(roles={"HR Officer"})
    @PostMapping(value ="/saveUser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> savePortalUser(
            @RequestHeader("Authorization") String authorization,
            @Valid @ModelAttribute PortalUserRequestDto requestDto,
            @RequestParam("photo") MultipartFile photo) {
        log.info("Create portal user request received, photo='{}'", photo != null ? photo.getOriginalFilename() : "none");
        PortalUserResponseDto response = portalUserService.createPortalUser(requestDto,photo);
        log.info("Token Recieved: {}", authorization);
        log.info("Portal user created: portalUserId='{}'", response.getPortalUserId());

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
    @PostMapping("/getAllPortalUsers")
    public ResponseEntity<ApiResponse<Page<PortalUserResponseDto>>> getAllPortalUsers(
            @RequestBody(required = false) PortalUserFilterRequestDto filterDto, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_LIST_FETCHED,
                        portalUserService.getAllPortalUsers(filterDto,page,size)));
    }

    // ------------------------------------------------------------------ POST: Update

    @Operation(summary = "Update Portal User",
            description = "multipart/form-data only — same flat fields as create, 'photo' is optional (only send it when replacing the existing photo)."
    )
    @Authorize(roles={"HR Officer"})
    @PutMapping(value = "/update/{portalUserId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> updatePortalUser(
            @PathVariable String portalUserId,
            @Valid @ModelAttribute PortalUserRequestDto requestDto,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        log.info("Update portal user request, portalUserId='{}'", portalUserId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_UPDATED,
                        portalUserService.updatePortalUser(portalUserId, requestDto,photo)));
    }

    // ------------------------------------------------------------------ PATCH: Soft Delete

    @Operation(summary = "Soft Delete Portal User")
    @PatchMapping("/changeStatus/{portalUserId}")
    public ResponseEntity<ApiResponse<PortalUserResponseDto>> changeStatus(
            @PathVariable String portalUserId,
            @RequestParam Boolean isActive) {
        log.info("Change status request: portalUserId='{}', isActive={}", portalUserId, isActive);

        return ResponseEntity.ok(
                ApiResponse.success(
                        PortalUserConstants.USER_STATUS_CHANGED,
                        portalUserService.changeStatus(portalUserId,isActive)));
    }
}

