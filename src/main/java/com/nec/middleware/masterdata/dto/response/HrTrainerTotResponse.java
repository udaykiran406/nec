package com.nec.middleware.masterdata.dto.response;

import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrTrainerTotResponse {
    private Long id;

    private String code;

    private String fullName;

    private String phone;

    private String email;

    private String description;

    private Boolean isActive;

    private Boolean isDeleted;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
