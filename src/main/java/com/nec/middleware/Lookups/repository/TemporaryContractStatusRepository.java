package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.TemporaryContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemporaryContractStatusRepository extends JpaRepository<TemporaryContractStatus, Long> {
}