package com.nec.middleware.workflow.service;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.repository.TemporaryContractRepository;
import com.nec.middleware.masterdata.entity.ApprovalWorkflowLevel;
import com.nec.middleware.masterdata.repository.ApprovalWorkflowLevelRepository;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("approvalLevelDelegate")
@RequiredArgsConstructor
public class ApprovalLevelDelegate
        implements JavaDelegate {

    private final ApprovalWorkflowLevelRepository approvalWorkflowLevelRepository;
    TemporaryContractRepository temporaryContractRepository;

    @Override
    public void execute(DelegateExecution execution) {

        String entityId =
                (String) execution.getVariable("entityId");
        Integer currentLevel =
                (Integer) execution.getVariable(
                        "approvalLevel");

        String moduleName =
                (String) execution.getVariable(
                        "moduleName");

        Integer nextLevel =
                currentLevel + 1;

        Optional<ApprovalWorkflowLevel> level =
                approvalWorkflowLevelRepository
                        .findByModuleNameAndLevelOrder(
                                moduleName,
                                nextLevel);
                temporaryContractRepository.findByContractId(entityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Contract not found"));
        if (level.isPresent()) {

            execution.setVariable(
                    "approvalLevel",
                    level.get().getLevelOrder());

            execution.setVariable(
                    "approvalRole",
                    level.get().getApprovalRole());

            execution.setVariable(
                    "currentApproval",
                    level.get().getApprovalRole());

            execution.setVariable(
                    "hasNextLevel",
                    true);
            // Update DB
//            contract.setCurrentApproval(
//                    level.get().getApprovalRole());
//
//            testContractorRepository.save(contract);

        } else {

            execution.setVariable(
                    "hasNextLevel",
                    false);

//            contract.setCurrentApproval(null);
//
//            contract.setStatus(
//                    "APPROVED"
//                            );
//
//            testContractorRepository.save(contract);
        }
    }
}