package com.nec.middleware.hr.controller;


import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.constant.PoliticalPartyAgentConstants;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentFilterRequestDto;
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
@RequestMapping("/api/hr/politicalPartyAgents")
@RequiredArgsConstructor
public class PoliticalPartyAgentController {

    private final PoliticalPartyAgentService politicalPartyAgentService;

    // ------------------------------------------------------------------ POST: Save / Update
    @Operation(summary = "Save Political Party Agent")
    @PostMapping("/savePartyAgent")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> createPartyAgent(
            @Valid @RequestBody PoliticalPartyAgentRequestDto PartyAgentRequestDto) {

        PoliticalPartyAgentResponseDto politicalPartyAgentResponseDto = politicalPartyAgentService.savePartyAgent(PartyAgentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(PoliticalPartyAgentConstants.AGENT_CREATED, politicalPartyAgentResponseDto));

    }

    // ------------------------------------------------------------------ GET: By ID

    @Operation(summary = "Get Political Party Agent by ID")
    @GetMapping("/{partyAgentUserId}")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> getAgentById(
            @PathVariable String partyAgentUserId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_FETCHED,
                        politicalPartyAgentService.getPolticalPartyAgentById(partyAgentUserId)));
    }

    // ------------------------------------------------------------------ GET: List
    @Operation(summary = "Get All Political Party Agents")
    @PostMapping("/getAllPoliticalPartyAgents")
    public ResponseEntity<ApiResponse<Page<PoliticalPartyAgentResponseDto>>> getAllPoliticalPartyAgent(
            @RequestBody(required = false) PoliticalPartyAgentFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PoliticalPartyAgentResponseDto> response =
                politicalPartyAgentService.getAllPartyAgents(
                        filterDto,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_LIST_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ POST: Soft Delete
    @Operation(summary = "Change Status Political Party Agent")
    @PatchMapping("/changeStatus/{partyAgentUserId}")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> changeStatus(
            @PathVariable String partyAgentUserId,
            @RequestParam Boolean isActive) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_STATUS_CHANGED,
                        politicalPartyAgentService.changeStatus(partyAgentUserId,isActive)));
    }

    @Operation(summary = "Update Political Party Agent")
    @PatchMapping("update/{partyAgentUserId}")
    public ResponseEntity<ApiResponse<PoliticalPartyAgentResponseDto>> updatePoliticalPartyAgent(
            @PathVariable String partyAgentUserId,
            @RequestBody PoliticalPartyAgentRequestDto requestDto) {

        PoliticalPartyAgentResponseDto partyAgent =
                politicalPartyAgentService.updatePoliticalPartyAgent(partyAgentUserId, requestDto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        PoliticalPartyAgentConstants.AGENT_UPDATED,
                        partyAgent
                )
        );
    }
}