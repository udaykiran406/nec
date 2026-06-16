package com.nec.middleware.workflow.dto.response;

import com.nec.middleware.dto.IdValueDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowSubmissionResponseDto {

    private String referenceId;

    private String moduleName;

    private String currentApproval;

    private String processInstanceId;

    private IdValueDto status;
}
