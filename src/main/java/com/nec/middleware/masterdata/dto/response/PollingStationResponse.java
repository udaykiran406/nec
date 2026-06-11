package com.nec.middleware.masterdata.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollingStationResponse {

    private Long id;

    private String pollingStationName;

    private String pollingStationCode;

    private Integer voterCapacity;

    private Long regionId;

    private Long districtId;

    private Long cityId;

    private String status;

    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Short isDeleted;
}