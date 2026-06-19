package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<MasterDataDistrict, Long> {

    Optional<MasterDataDistrict> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataDistrict> findAllByIsDeletedOrderByDistrictNameAsc(Short isDeleted);

    boolean existsByDistrictNameIgnoreCaseAndRegionIdAndIsDeleted(
            String districtName,
            Long regionId,
            Short isDeleted
    );

    boolean existsByDistrictNameIgnoreCaseAndRegionIdAndIsDeletedAndIdNot(
            String districtName,
            Long regionId,
            Short isDeleted,
            Long id
    );
    @Query("""
    SELECT d
    FROM MasterDataDistrict d
    WHERE d.isDeleted = 0
      AND (:regionId IS NULL OR d.regionId = :regionId)
    ORDER BY d.districtName
    """)
    Page<MasterDataDistrict> findDistricts(
            @Param("regionId") Long regionId,
            Pageable pageable
    );

}