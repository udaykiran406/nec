package com.nec.middleware.idGenerator.repository;

import com.nec.middleware.idGenerator.entity.IdGenerator;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdGeneratorRepository
        extends JpaRepository<IdGenerator, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<IdGenerator> findByModuleCode(String moduleCode);
}
