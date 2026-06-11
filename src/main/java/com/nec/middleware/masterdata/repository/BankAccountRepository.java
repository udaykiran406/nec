package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository
        extends JpaRepository<MasterDataBankAccount, Long> {

    Optional<MasterDataBankAccount> findByIdAndIsDeleted(
            Long id,
            Short isDeleted
    );

    List<MasterDataBankAccount>
    findAllByIsDeletedOrderByAccountHolderNameAsc(
            Short isDeleted
    );

    boolean existsByAccountNumberAndIsDeleted(
            String accountNumber,
            Short isDeleted
    );

    boolean existsByAccountNumberAndIsDeletedAndIdNot(
            String accountNumber,
            Short isDeleted,
            Long id
    );

    @Query("""
        SELECT b
        FROM MasterDataBankAccount b
        WHERE b.isDeleted = 0
          AND (
                :accountType IS NULL
                OR TRIM(:accountType) = ''
                OR b.accountType = :accountType
              )
          AND (
                :accountHolderName IS NULL
                OR TRIM(:accountHolderName) = ''
                OR LOWER(b.accountHolderName)
                   LIKE CONCAT('%', LOWER(:accountHolderName), '%')
              )
          AND (
                :bankName IS NULL
                OR TRIM(:bankName) = ''
                OR LOWER(b.bankName)
                   LIKE CONCAT('%', LOWER(:bankName), '%')
              )
          AND (
                :branchName IS NULL
                OR TRIM(:branchName) = ''
                OR LOWER(b.branchName)
                   LIKE CONCAT('%', LOWER(:branchName), '%')
              )
        ORDER BY b.accountHolderName
        """)
    Page<MasterDataBankAccount> findBankAccounts(
            @Param("accountType") String accountType,
            @Param("accountHolderName") String accountHolderName,
            @Param("bankName") String bankName,
            @Param("branchName") String branchName,
            Pageable pageable
    );
}