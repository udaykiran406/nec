package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository
        extends JpaRepository<MasterDataWarehouse, Long> {

    Optional<MasterDataWarehouse> findByIdAndIsDeleted(
            Long id,
            Short isDeleted
    );

    List<MasterDataWarehouse>
    findAllByIsDeletedOrderByWarehouseNameAsc(
            Short isDeleted
    );

    boolean existsByWarehouseNameIgnoreCaseAndIsDeleted(
            String warehouseName,
            Short isDeleted
    );

    boolean existsByWarehouseNameIgnoreCaseAndIsDeletedAndIdNot(
            String warehouseName,
            Short isDeleted,
            Long id
    );

    @Query("""
        SELECT w
        FROM MasterDataWarehouse w
        WHERE w.isDeleted = 0
          AND (:regionId IS NULL OR w.regionId = :regionId)
          AND (:districtId IS NULL OR w.districtId = :districtId)
          AND (:cityId IS NULL OR w.cityId = :cityId)
        ORDER BY w.warehouseName
        """)
    Page<MasterDataWarehouse> findWarehouses(
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );
}