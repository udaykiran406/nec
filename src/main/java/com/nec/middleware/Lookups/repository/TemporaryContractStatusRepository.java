package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.TemporaryContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemporaryContractStatusRepository extends JpaRepository<TemporaryContractStatus, Long> {
//    optional <TemporaryContractStatus> findByValue(String approved);

    Optional<TemporaryContractStatus> findByValue(String approved);
}