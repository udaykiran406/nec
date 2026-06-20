package com.nec.middleware.hr.entity;

import com.nec.middleware.Lookups.entity.ContractTypes;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "temporary_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryContract extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "contract_Id", nullable = false, unique = true, length = 30)
    private String contractId;

    @Column(name = "employer_name", nullable = false, length = 200)
    private String employerName;

    @Column(name = "employee_name", nullable = false, length = 150)
    private String employeeName;

    @Column(name = "contact_no", nullable = false, unique = true, length = 30)
    private String contactNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractTypes contractType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name = "job_description",
            columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "terms_and_conditions",
            columnDefinition = "TEXT")
    private String termsAndConditions;
    @Column(name = "total_contract_amount",
            nullable = false,
            precision = 18,
            scale = 2)
    private BigDecimal totalContractAmount;

    @Column(name = "initial_payment_percentage",
            nullable = false,
            precision = 5,
            scale = 2)
    private BigDecimal initialPaymentPercentage;

    @Column(name = "remaining_percentage",
            nullable = false,
            precision = 5,
            scale = 2)
    private BigDecimal remainingPercentage;

    @Column(name = "initial_payment_amount",
            precision = 18,
            scale = 2)
    private BigDecimal initialPaymentAmount;

    @Column(name = "remaining_payment_amount",
            precision = 18,
            scale = 2)
    private BigDecimal remainingPaymentAmount;
    @Column(name = "process_instance_id")
    private String processInstanceId;

    //HR Approval
    //DG Approval
    //Finance Approval
    @Column(name = "current_approval")
    private String currentApproval;

    //    Manual Completion
//    Deployment Completion
//    Finance Confirmation
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "completion_trigger_id")
//    private ContractCompletionTrigger completionTrigger;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "status_id")
    private String status;

    @Column(name = "rejected_remarks", length = 1000)
    private String rejectedRemarks;

    //Payment Tracking
//    @Column(name = "initial_payment_released")
//    private Boolean initialPaymentReleased;
//
//    @Column(name = "final_payment_released")
//    private Boolean finalPaymentReleased;

    //Contract Lifecycle

    //    @Column(name = "completed_date")
//    private LocalDate completedDate;
//
//    @Column(name = "closed_date")
//    private LocalDate closedDate;
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "revision_no")
    private Integer revisionNo;

}