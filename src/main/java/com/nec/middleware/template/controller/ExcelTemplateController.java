package com.nec.middleware.template.controller;

import com.nec.middleware.hr.dto.request.*;
import com.nec.middleware.rbacAuth.rbac.dto.request.RbacUserBulkUploadDto;
import com.nec.middleware.template.service.ExcelTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Generic controller for downloading Excel import templates.
 * Lives in the shared "template" package so any module (hr, rbac, etc.)
 * can add its own endpoint here without those modules depending on each other.
 *
 * Pattern for adding a new module's template:
 *   1. Add @ExcelColumn to that module's DTO fields.
 *   2. Add one @GetMapping method below, calling buildResponse(SomeDto.class, fileName).
 */
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
@Tag(name = "Excel Templates", description = "Generic Excel import-template generation for any module's DTO")
public class ExcelTemplateController {

    private final ExcelTemplateService excelTemplateService;


    @GetMapping("/portalUser")
    @Operation(
            summary = "Download Portal User Excel import template",
            description = "Generates Excel template from PortalUserRequestDto"
    )
    public ResponseEntity<byte[]> portalUserTemplate() throws Exception {
        return buildResponse(
                PortalUserRequestDto.class,
                "portal_user_template.xlsx"
        );
    }

    @GetMapping("/universityTrainee")
    @Operation(
            summary = "Download University Trainee Excel import template",
            description = "Generates a ready-to-fill .xlsx template (header row, sample row, "
                    + "cell comments, and dropdowns where applicable) based on the @ExcelColumn "
                    + "annotations declared on UniversityTraineeRequestDto."
    )
    public ResponseEntity<byte[]> universityTraineeTemplate() throws Exception {
        return buildResponse(UniversityTraineeRequestDto.class, "university_trainee_template.xlsx");
    }

    @GetMapping("/politicalPartyAgent")
    @Operation(
            summary = "Download Political Party Agent Excel import template"
    )
    public ResponseEntity<byte[]> politicalPartyAgentTemplate() throws Exception {
        return buildResponse(
                PoliticalPartyAgentRequestDto.class,
                "political_party_agent_template.xlsx"
        );
    }

    @GetMapping("/ministryOfInteriors")
    @Operation(
            summary = "Download Ministry Of Interior Excel import template"
    )
    public ResponseEntity<byte[]> ministryOfInterior() throws Exception {
        return buildResponse(
                MinistryofInteriorRequestDto.class,
                "ministry_of_interior_template.xlsx"
        );
    }

    @GetMapping("/temporaryContracts")
    @Operation(
            summary = "Download Temporary Contract Excel import template"
    )
    public ResponseEntity<byte[]> temporaryContractTemplate() throws Exception {
        return buildResponse(
                TemporaryContractRequestDto.class,
                "temporary_contract_template.xlsx"
        );
    }

    @GetMapping("/RbacUsers")
    @Operation(
            summary = "Download RBAC Users Excel import template"
    )
    public ResponseEntity<byte[]> RbacUsers() throws Exception {
        return buildResponse(
                RbacUserBulkUploadDto.class,
                "RbacUsers_template.xlsx"
        );
    }


    private ResponseEntity<byte[]> buildResponse(Class<?> dtoClass, String fileName) throws Exception {
        byte[] excelBytes = excelTemplateService.createTemplate(dtoClass);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}