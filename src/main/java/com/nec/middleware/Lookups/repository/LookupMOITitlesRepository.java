package com.nec.middleware.Lookups.repository;

import com.nec.middleware.Lookups.entity.MinistryOfInteriorTitles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LookupMOITitlesRepository extends JpaRepository<MinistryOfInteriorTitles, Long> {
}
