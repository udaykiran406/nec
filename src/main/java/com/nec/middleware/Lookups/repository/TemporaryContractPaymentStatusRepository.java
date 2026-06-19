package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.TemporaryContractPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryContractPaymentStatusRepository
        extends JpaRepository<TemporaryContractPaymentStatus, Long> {
}
