package com.nec.middleware.hr.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversityTraineeFilterRequestDto {

    // Search fields
    private String universityTraineeId;   // partial match on business key
    private String fullName;              // partial, case-insensitive

    // FK filters — traverse associations in the Specification
    private Long genderId;
    private Long paymentMethodId;
    private Long universityId;
    private Long regionId;
    private Long districtId;
    private Long cityId;
    private String status;
    private Boolean isActive;

}