package com.nec.middleware.Lookups.repository;


import com.nec.middleware.Lookups.entity.PortalUserTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LookupPortalUserTypeRepository extends JpaRepository<PortalUserTypes, Long> {
}
