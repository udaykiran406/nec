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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(
        name = "Ministry of Interior",
        description = "Ministry of Interior Management APIs"
)
@RestController
@RequestMapping("/api/v1/hr/ministry-of-interior")
@RequiredArgsConstructor
public class MinistryofInteriorController {

    private final MinistryofInteriorService ministryofInteriorService;

    // ------------------------------------------------------------------ SAVE

    @Operation(summary = "Create Ministry of Interior",
            description = "multipart/form-data: 'requestDto' part is the JSON payload, 'photo' part is the image file (jpg/jpeg/png/webp, max 5MB).")
    @PostMapping(value= "/save", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> createMinistryofInterior(
            @Valid @ModelAttribute MinistryofInteriorRequestDto requestDto,
            @RequestParam("photo") MultipartFile photo) {
        log.info("Create ministry of interior request received, photo='{}'", photo != null ? photo.getOriginalFilename() : "none");

        MinistryofInteriorResponseDto response = ministryofInteriorService.saveMinistryofInterior(requestDto, photo);
        log.info("Ministry of interior created: ministryofInteriorId='{}'", response.getMinistryofInteriorId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_CREATED,
                        response));
    }

    // ------------------------------------------------------------------ UPDATE

    @Operation(summary = "Update Ministry of Interior",
            description = "multipart/form-data: flat fields + optional photo")
    @PutMapping(value="/{ministryofInteriorId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> updateMinistryofInterior(
            @PathVariable String ministryofInteriorId,
            @Valid @ModelAttribute MinistryofInteriorRequestDto requestDto,
            @RequestParam (value = "photo", required = false) MultipartFile photo) {
        log.info("Update ministry of interior request, ministryofInteriorId='{}'", ministryofInteriorId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_UPDATED,
                        ministryofInteriorService.updateMinistryofInterior(
                                ministryofInteriorId,
                                requestDto,
                                photo)));
    }

    // ------------------------------------------------------------------ GET BY ID

    @Operation(summary = "Get Ministry of Interior by ID")
    @GetMapping("/{ministryofInteriorId}")
    public ResponseEntity<ApiResponse<MinistryofInteriorResponseDto>> getMinistryofInteriorById(
            @PathVariable String ministryofInteriorId) {
        log.info("Get ministry of interior request: ministryofInteriorId='{}'", ministryofInteriorId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_FETCHED,
                        ministryofInteriorService.getMinistryofInteriorById(ministryofInteriorId)
                )
        );
    }

    // ------------------------------------------------------------------ GET ALL

    @Operation(summary = "Get Paginated & Filtered Ministry of Interior List")
    @GetMapping("/getAll")
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
        log.info("Change status request: ministryofInteriorId='{}', isActive={}", ministryofInteriorId, isActive);

        return ResponseEntity.ok(
                ApiResponse.success(
                        MinistryofInteriorConstants.MINISTRY_OF_INTERIOR_STATUS_CHANGED,
                        ministryofInteriorService.changeStatus(
                                ministryofInteriorId,
                                isActive)));
    }
}
