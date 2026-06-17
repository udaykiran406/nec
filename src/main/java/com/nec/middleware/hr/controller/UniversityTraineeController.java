package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.constant.UniversityTraineeConstants;
import com.nec.middleware.hr.dto.request.UniversityTraineeFilterRequestDto;
import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.BulkUploadResultDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.service.UniversityTraineeService;
import com.nec.middleware.hr.util.BulkErrorExcelWriter;
import com.nec.middleware.hr.util.UniversityTraineeCsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Tag(
        name = "University Trainee",
        description = "University Trainee Management APIs"
)
@RestController
@RequestMapping("/api/v1/hr/universityTrainee")
@RequiredArgsConstructor
public class UniversityTraineeController {

    private final UniversityTraineeService universityTraineeService;
    private final UniversityTraineeCsvImportService csvImportService;
    private final BulkErrorExcelWriter bulkErrorExcelWriter;


    // ------------------------------------------------------------------ CREATE

    @Operation(summary = "Create University Trainee",
            description = "multipart/form-data: flat fields + photo file")
    @PostMapping(   value = "/saveTrainee",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> createUniversityTrainee(
            @Valid @ModelAttribute UniversityTraineeRequestDto requestDto,
            @RequestParam("photo")MultipartFile photo) {

        UniversityTraineeResponseDto universityTraineeResponse =
                universityTraineeService.createTrainee(requestDto, photo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        UniversityTraineeConstants.TRAINEE_CREATED,
                        universityTraineeResponse
                ));
    }
// ------------------------------------------------------------------ BULK CREATE

    @Operation(
            summary = "Bulk Upload University Trainees",
            description = """
                    Upload an Excel (.xlsx) or CSV file to create multiple University Trainees in one request.

                    **Expected columns (header row required, order-independent):**
                    Full Name | Gender ID | Age | Phone | Email | Payment Method ID |
                    University ID | Semester | Faculty | Region ID | District ID | City ID | Status ID

                    **Response behaviour:**
                    - If every row succeeds → returns JSON (`ApiResponse<BulkUploadResultDto>`) with the saved records.
                    - If one or more rows fail → returns the failed rows as a downloadable `.xlsx` file
                      (original columns + an appended "Error Reason" column). Successful rows are still
                      saved to the database in this case; only the response body changes.

                    **Photos:** not supported in bulk mode — photoPath will be null for bulk-created records.
                    Use the single update endpoint to attach photos afterwards.
                    """
    )
    @PostMapping(value = "/bulkUpload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUpload(@RequestParam("file") MultipartFile file) {

        log.info("Bulk upload request received, filename='{}'", file.getOriginalFilename());

        BulkUploadResultDto<UniversityTraineeResponseDto> result =
                universityTraineeService.bulkCreate(file);

        // ── All rows succeeded → normal JSON response ──────────────────
        if (result.getFailureCount() == 0) {
            String message = String.format(
                    "Bulk upload complete: all %d rows saved successfully",
                    result.getSuccessCount());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(message, result));
        }

        // ── Some or all rows failed → stream the error report file ─────
        File errorFile = bulkErrorExcelWriter.write(
                csvImportService.columnLabels(),
                result.getErrors(),
                "university-trainee-bulk-errors");

        log.info("Bulk upload had {} failures out of {} rows — returning error report file",
                result.getFailureCount(), result.getTotalRows());

        // IMPORTANT: the resource streams lazily — Spring reads the file
        // bytes AFTER this method returns, while writing the HTTP response body.
        // Deleting the file here (before returning) would race with that read
        // and intermittently produce truncated/empty downloads. Instead we wrap
        // it in a self-deleting InputStream so cleanup happens only once Spring
        // has fully read the file, not before.
        InputStreamResource selfDeletingResource;
        try {
            selfDeletingResource = new InputStreamResource(
                    new FileInputStream(errorFile) {
                        @Override
                        public void close() throws IOException {
                            super.close();
                            bulkErrorExcelWriter.cleanup(errorFile);
                        }
                    });
        } catch (FileNotFoundException e) {
            log.error("Could not open generated error report for streaming: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to stream bulk error report", e);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(errorFile.getName())
                                .build()
                                .toString())
                .header("X-Bulk-Success-Count", String.valueOf(result.getSuccessCount()))
                .header("X-Bulk-Failure-Count", String.valueOf(result.getFailureCount()))
                .header("X-Bulk-Total-Rows", String.valueOf(result.getTotalRows()))
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(errorFile.length())
                .body(selfDeletingResource);
    }

    // ------------------------------------------------------------------ UPDATE

    @Operation(summary = "Update University Trainee",
            description = "multipart/form-data: flat fields + optional photo")
    @PutMapping(value = "/{universityTraineeId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> updateUniversityTrainee(
            @PathVariable String universityTraineeId,
            @Valid @ModelAttribute UniversityTraineeRequestDto requestDto,
            @RequestParam(value="photo", required = false) MultipartFile photo) {

        UniversityTraineeResponseDto response =
                universityTraineeService.updateTrainee(
                        universityTraineeId,
                        requestDto,
                        photo
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_UPDATED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ GET BY BUSINESS ID

    @Operation(summary = "Get University Trainee by University Trainee ID")
    @GetMapping("/{universityTraineeId}")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> getByUniversityTraineeId(
            @PathVariable String universityTraineeId) {

        UniversityTraineeResponseDto response =
                universityTraineeService.getTraineeByUniversityTraineeId(
                        universityTraineeId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ GET ALL

    @Operation(summary = "Get All University Trainees")
    @PostMapping("/getAllUniversityTrainees")
    public ResponseEntity<ApiResponse<Page<UniversityTraineeResponseDto>>> getAllUniversityTrainee(
            @RequestBody(required = false) UniversityTraineeFilterRequestDto request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UniversityTraineeResponseDto> response =
                universityTraineeService.getAllUniversityTrainees(
                        request,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_LIST_FETCHED,
                        response
                )
        );
    }

    // ------------------------------------------------------------------ CHANGE STATUS

    @Operation(summary = "Change University Trainee Status")
    @PatchMapping("/{universityTraineeId}/status")
    public ResponseEntity<ApiResponse<UniversityTraineeResponseDto>> changeUniversityTraineeStatus(
            @PathVariable String universityTraineeId,
            @RequestParam Boolean isActive) {

        UniversityTraineeResponseDto response =
                universityTraineeService.changeStatus(
                        universityTraineeId,
                        isActive
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        UniversityTraineeConstants.TRAINEE_STATUS_CHANGED,
                        response
                )
        );
    }
}