package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.ContractPaymentTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractPaymentTypeRepository extends JpaRepository<ContractPaymentTypes, Long> {
}
