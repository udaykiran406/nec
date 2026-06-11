package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.LookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LookupRepository extends JpaRepository<LookupEntity, Long> {

    Optional<LookupEntity> findByTableNameAndIsActive(String tableName, Integer isActive);

    boolean existsByTableName(String tableName);

    List<LookupEntity> findByIsActiveOrderByTableNameAsc(Integer isActive);
}