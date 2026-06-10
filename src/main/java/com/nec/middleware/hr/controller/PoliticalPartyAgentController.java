package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.constant.PoliticalPartyAgentConstants;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentListRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.service.PoliticalPartyAgentService;
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
        name = "Political Party Agent",
        description = "Political Party Agent Management APIs"
)
@RestController
@RequestMapping("/api/hr/political-party-agents")
@RequiredArgsConstructor
public class PoliticalPartyAgentController {

    private final PoliticalPartyAgentService service;

    // ------------------------------------------------------------------ POST: Save / Update
    @Operation(summary = "Save or Update Political Party Agent")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> saveOrUpdate(
            @Valid @RequestBody PoliticalPartyAgentRequestDto requestDto) {

        PoliticalPartyAgentResponseDto response = service.saveOrUpdate(requestDto);

        boolean isCreate = requestDto.getId() == null;
        String message   = isCreate
                ? PoliticalPartyAgentConstants.AGENT_CREATED
                : PoliticalPartyAgentConstants.AGENT_UPDATED;

        return isCreate
                ? ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message, response))
                : ResponseEntity.ok(ApiResponse.success(message, response));
    }

    // ------------------------------------------------------------------ GET: By ID

    @Operation(summary = "Get Political Party Agent by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_FETCHED,
                        service.getById(id)));
    }

    // ------------------------------------------------------------------ GET: List
    @Operation(summary = "Get Paginated & Filtered Political Party Agent List")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PoliticalPartyAgentResponseDto>>> getAll(
            @ModelAttribute PoliticalPartyAgentListRequestDto filterDto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_LIST_FETCHED,
                        service.getAll(filterDto)));
    }

    // ------------------------------------------------------------------ POST: Toggle Status
    @Operation(summary = "Toggle Political Party Agent Active Status")
    @PostMapping("/status/{id}")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> changeStatus(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_STATUS_CHANGED,
                        service.changeStatus(id)));
    }

    // ------------------------------------------------------------------ POST: Soft Delete
    @Operation(summary = "Soft Delete Political Party Agent")
    @PostMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> softDelete(
            @PathVariable Long id) {

        service.softDelete(id);
        return ResponseEntity.ok(
                ApiResponse.success(PoliticalPartyAgentConstants.AGENT_DELETED, null));
    }
}