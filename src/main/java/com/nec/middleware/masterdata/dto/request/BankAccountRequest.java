package com.nec.middleware.masterdata.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountRequest {

    private Long id;

    @NotBlank
    @Size(max = 200)
    private String accountHolderName;

    @NotBlank
    @Size(max = 100)
    private String accountNumber;

    @NotBlank
    @Size(max = 100)
    private String accountType;

    @NotBlank
    @Size(max = 200)
    private String bankName;

    @NotBlank
    @Size(max = 200)
    private String branchName;

    @NotBlank
    @Size(max = 50)
    private String ifscSwiftCode;

    @NotBlank
    @Pattern(regexp = "^(Active|Inactive)$")
    private String status;

    private Long createdBy;

    private Long updatedBy;
}