package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollingStationListRequestDto {

    private Long regionId;

    private Long districtId;

    private Long cityId;

    @Min(value = 0, message = "Page index must be 0 or greater")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private int size = 20;
}
