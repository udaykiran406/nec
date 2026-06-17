package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.service.TemporaryContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(
        name = "Temporary Contract",
        description = "Temporary Contract Management APIs"
)
@RestController
@RequestMapping("/api/v1/hr/temporaryContract")
@RequiredArgsConstructor
public class TemporaryContractController {

    private final TemporaryContractService temporaryContractService;

    @Operation(summary = "Create or Update Temporary Contract",
            description = "Creates a new temporary contract or updates an existing one based on the provided details. " +
                    "If the request contains an ID, it will attempt to update the corresponding contract; otherwise, it will create a new contract.")
    @PostMapping("/createOrUpdateTemporaryContract")
    public ResponseEntity<ApiResponse<TemporaryContractResponseDto>> createorUpdateTemporaryContract( @RequestBody @Valid TemporaryContractRequestDto requestDto) {

         return ResponseEntity.ok(
                ApiResponse.success(
                        "Temporary Contract Created Successfully",
                        temporaryContractService
                                .createOrUpdateContract(requestDto))
        );
    }
}
