package com.nec.middleware.masterdata.dto.response;

import lombok.*;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponse {
    private Long id;

    private String warehouseName;

    private Long regionId;

    private Long districtId;

    private Long cityId;

    private String purpose;

    private String status;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Short isDeleted;
}
