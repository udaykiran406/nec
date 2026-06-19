package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.DirectorAppraisalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorAppraisalStatusRepository extends JpaRepository<DirectorAppraisalStatus, Long> {
}