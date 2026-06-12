package com.nec.middleware.hr.controller;

import com.nec.middleware.hr.dto.request.TrainingClassRequest;
import com.nec.middleware.hr.dto.response.ApiResponse;
import com.nec.middleware.hr.dto.response.TrainingClassResponse;
import com.nec.middleware.hr.service.TrainingClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(
        name = "Training Classes",
        description = "HR Training Class Management APIs"
)
@RestController
@RequestMapping("/api/training/classes")
@RequiredArgsConstructor
public class TrainingClassController {

    private final TrainingClassService trainingClassService;

    @Operation(summary = "Create or Update Training Class")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<TrainingClassResponse>> saveTrainingClass(
            @Valid @RequestBody TrainingClassRequest request) {

        TrainingClassResponse response = trainingClassService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Training class saved successfully", response));
    }

    @Operation(summary = "Get Training Class by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainingClassResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training class fetched successfully",
                        trainingClassService.getById(id)
                )
        );
    }

    @Operation(summary = "Get All Training Classes")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TrainingClassResponse>>> getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training classes fetched successfully",
                        trainingClassService.getAll()
                )
        );
    }

    @Operation(summary = "Soft Delete Training Class")
    @PatchMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        trainingClassService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Training class deleted successfully",
                        "Deleted"
                )
        );
    }
}