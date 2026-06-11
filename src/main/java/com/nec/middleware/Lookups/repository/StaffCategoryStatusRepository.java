package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.StaffCategoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffCategoryStatusRepository extends JpaRepository<StaffCategoryStatus, Long> {
}