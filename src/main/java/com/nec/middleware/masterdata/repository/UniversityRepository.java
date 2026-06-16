package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import com.nec.middleware.masterdata.entity.MasterDataVoterRegistrationCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UniversityRepository extends JpaRepository<MasterDataUniversity, Long> {

    Optional<MasterDataUniversity> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataUniversity> findAllByIsDeletedOrderByUniversityNameAsc(Short isDeleted);

    boolean existsByUniversityNameIgnoreCaseAndIsDeleted(
            String universityName,
            Short isDeleted
    );

    boolean existsByUniversityNameIgnoreCaseAndIsDeletedAndIdNot(
            String universityName,
            Short isDeleted,
            Long id
    );


    @Query("""
        SELECT u
        FROM MasterDataUniversity u
        WHERE u.isDeleted = 0
          AND (:regionId IS NULL OR u.regionId = :regionId)
          AND (:districtId IS NULL OR u.districtId = :districtId)
          AND (:cityId IS NULL OR u.cityId = :cityId)
        ORDER BY u.universityName
        """)
    Page<MasterDataUniversity> findUniversities(
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );
}