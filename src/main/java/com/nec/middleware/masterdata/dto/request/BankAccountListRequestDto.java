package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountListRequestDto {

    private String accountType;

    private String accountHolderName;

    private String bankName;

    private String branchName;

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 20;
}
