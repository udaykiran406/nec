package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataPollingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PollingStationRepository extends JpaRepository<MasterDataPollingStation, Long> {

    Optional<MasterDataPollingStation> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataPollingStation> findAllByIsDeletedOrderByPollingStationNameAsc(Short isDeleted);

    boolean existsByPollingStationCodeIgnoreCaseAndIsDeleted(
            String pollingStationCode,
            Short isDeleted
    );

    boolean existsByPollingStationCodeIgnoreCaseAndIsDeletedAndIdNot(
            String pollingStationCode,
            Short isDeleted,
            Long id
    );

    @Query("""
        SELECT p
        FROM MasterDataPollingStation p
        WHERE p.isDeleted = 0
          AND (:regionId IS NULL OR p.regionId = :regionId)
          AND (:districtId IS NULL OR p.districtId = :districtId)
          AND (:cityId IS NULL OR p.cityId = :cityId)
        ORDER BY p.pollingStationName
        """)
    Page<MasterDataPollingStation> findPollingStations(
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );
}