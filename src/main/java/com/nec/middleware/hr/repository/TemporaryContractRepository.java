package com.nec.middleware.hr.repository;

import com.nec.middleware.hr.entity.TemporaryContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemporaryContractRepository extends JpaRepository<TemporaryContract, Long> {

    Optional<TemporaryContract> findTopByOrderByIdDesc();
    Optional<TemporaryContract> findByContractId(String contractId);
}
