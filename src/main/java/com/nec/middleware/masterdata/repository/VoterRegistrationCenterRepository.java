package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataVoterRegistrationCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoterRegistrationCenterRepository
        extends JpaRepository<MasterDataVoterRegistrationCenter, Long> {

    Optional<MasterDataVoterRegistrationCenter> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataVoterRegistrationCenter> findAllByIsDeletedOrderByVrcNameAsc(Short isDeleted);

    boolean existsByVrcCodeIgnoreCaseAndIsDeleted(String vrcCode, Short isDeleted);

    boolean existsByVrcCodeIgnoreCaseAndIsDeletedAndIdNot(
            String vrcCode,
            Short isDeleted,
            Long id
    );

    @Query("""
        SELECT v
        FROM MasterDataVoterRegistrationCenter v
        WHERE v.isDeleted = 0
          AND (:regionId IS NULL OR v.regionId = :regionId)
          AND (:districtId IS NULL OR v.districtId = :districtId)
          AND (:cityId IS NULL OR v.cityId = :cityId)
        ORDER BY v.vrcName
        """)
    Page<MasterDataVoterRegistrationCenter> findVoterRegistrationCenters(
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );
}