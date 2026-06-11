package com.nec.middleware.masterdata.repository;

import com.nec.middleware.masterdata.entity.MasterDataAaqilType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AaqilTypeRepository
        extends JpaRepository<MasterDataAaqilType, Long> {

    Optional<MasterDataAaqilType> findByIdAndIsDeleted(
            Long id,
            Boolean isDeleted
    );

    List<MasterDataAaqilType>
    findAllByIsDeletedOrderByValueAsc(
            Boolean isDeleted
    );

    boolean existsByCodeIgnoreCaseAndIsDeleted(
            String code,
            Boolean isDeleted
    );

    boolean existsByCodeIgnoreCaseAndIsDeletedAndIdNot(
            String code,
            Boolean isDeleted,
            Long id
    );

    Page<MasterDataAaqilType> findAllByIsDeletedOrderByValueAsc(Boolean isDeleted, Pageable pageable);
}