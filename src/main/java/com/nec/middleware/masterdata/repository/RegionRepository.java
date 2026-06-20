package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository
        extends JpaRepository<MasterDataRegion, Long> {
}
