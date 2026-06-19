package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<MasterDataCity, Long> {

    Optional<MasterDataCity> findByIdAndIsDeleted(Long id, Short isDeleted);

    List<MasterDataCity> findAllByIsDeletedOrderByCityNameAsc(Short isDeleted);

    boolean existsByCityNameIgnoreCaseAndDistrictIdAndIsDeleted(
            String cityName,
            Long districtId,
            Short isDeleted
    );

    boolean existsByCityNameIgnoreCaseAndDistrictIdAndIsDeletedAndIdNot(
            String cityName,
            Long districtId,
            Short isDeleted,
            Long id
    );
    Page<MasterDataCity> findAllByIsDeletedOrderByCityNameAsc(
            Short isDeleted,
            Pageable pageable
    );
    @Query("""
        SELECT c
        FROM MasterDataCity c
        WHERE c.isDeleted = 0
          AND (:regionId IS NULL OR c.regionId = :regionId)
          AND (:districtId IS NULL OR c.districtId = :districtId)
        ORDER BY c.cityName
        """)
    Page<MasterDataCity> findCities(
            @Param("regionId") Long regionId,
            @Param("districtId") Long districtId,
            Pageable pageable
    );


}