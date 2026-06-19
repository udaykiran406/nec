package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.Modules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LookupModulesRepository extends JpaRepository<Modules, Long> {
}
