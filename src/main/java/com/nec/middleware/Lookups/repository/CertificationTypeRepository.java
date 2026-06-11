package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.CertificationTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationTypeRepository extends JpaRepository<CertificationTypes, Long> {
}
