package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MasterDataRepository extends JpaRepository<MasterDataRegion, Long> {

    Optional<MasterDataRegion> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataRegion> findAllByIsDeletedOrderByRegionNameAsc(Short isDeleted);

    boolean existsByRegionNameIgnoreCaseAndIsDeleted(
            String regionName,
            Short isDeleted
    );

    boolean existsByRegionNameIgnoreCaseAndIsDeletedAndIdNot(
            String regionName,
            Short isDeleted,
            Long id
    );

    Page<MasterDataRegion> findAllByIsDeletedOrderByRegionNameAsc(Short isDeleted, Pageable pageable);
}