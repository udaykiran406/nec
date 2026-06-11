package com.nec.middleware.Lookups.controller;

import com.nec.middleware.Lookups.dto.ApiResponseDto;
import com.nec.middleware.Lookups.dto.LookupRequestDto;
import com.nec.middleware.Lookups.dto.LookupTableResponseDto;
import com.nec.middleware.Lookups.dto.LookupValueResponseDto;
import com.nec.middleware.Lookups.service.LookupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookups")
@RequiredArgsConstructor
@Tag(name = "Lookup Management", description = "Lookup APIs")
public class LookupController {

    private final LookupService lookupService;

    /**
     * API 1
     * Get all lookup table names
     */
    @GetMapping
    @Operation(summary = "Get all active lookup tables")
    public ResponseEntity<ApiResponseDto<List<LookupTableResponseDto>>> getAllLookupTables() {

        return ResponseEntity.ok(
                ApiResponseDto.<List<LookupTableResponseDto>>builder()
                        .success(true)
                        .message("Lookup table names retrieved successfully")
                        .data(lookupService.getAllActiveLookupTables())
                        .build()
        );
    }

    /**
     * API 2
     * Get lookup values by table name
     */
    @GetMapping("/{tableName}")
    @Operation(summary = "Get lookup values by table name")
    public ResponseEntity<ApiResponseDto<List<LookupValueResponseDto>>> getLookupValues(
            @PathVariable String tableName) {

        return ResponseEntity.ok(
                ApiResponseDto.<List<LookupValueResponseDto>>builder()
                        .success(true)
                        .message("Lookup values retrieved successfully")
                        .data(lookupService.getLookupValuesByTableName(tableName))
                        .build()
        );
    }

    /**
     * API 3
     * Add / Edit lookup value
     */
    @PostMapping
    @Operation(summary = "Add or Edit lookup value")
    public ResponseEntity<ApiResponseDto<LookupValueResponseDto>> addOrEditLookupValue(
            @Valid @RequestBody LookupRequestDto requestDto) {

        LookupValueResponseDto response =
                lookupService.addOrEditLookupValue(requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponseDto.<LookupValueResponseDto>builder()
                                .success(true)
                                .message(
                                        requestDto.getId() == null
                                                ? "Lookup value created successfully"
                                                : "Lookup value updated successfully"
                                )
                                .data(response)
                                .build()
                );
    }
}