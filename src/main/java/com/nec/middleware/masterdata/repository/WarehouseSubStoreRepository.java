package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataWarehouseSubStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseSubStoreRepository
        extends JpaRepository<MasterDataWarehouseSubStore, Long> {

    Optional<MasterDataWarehouseSubStore> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataWarehouseSubStore> findAllByIsDeletedOrderBySubStoreNameAsc(Short isDeleted);

    boolean existsBySubStoreNameIgnoreCaseAndWarehouseIdAndIsDeleted(
            String subStoreName,
            Long warehouseId,
            Short isDeleted
    );

    boolean existsBySubStoreNameIgnoreCaseAndWarehouseIdAndIsDeletedAndIdNot(
            String subStoreName,
            Long warehouseId,
            Short isDeleted,
            Long id
    );

    @Query("""
        SELECT s
        FROM MasterDataWarehouseSubStore s
        WHERE s.isDeleted = 0
          AND (:warehouseId IS NULL OR s.warehouseId = :warehouseId)
          AND (:regionId IS NULL OR s.regionId = :regionId)
          AND (:districtId IS NULL OR s.districtId = :districtId)
          AND (:cityId IS NULL OR s.cityId = :cityId)
        ORDER BY s.subStoreName
        """)
    Page<MasterDataWarehouseSubStore> findWarehouseSubStores(
            @Param("warehouseId") Long warehouseId,
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );
}