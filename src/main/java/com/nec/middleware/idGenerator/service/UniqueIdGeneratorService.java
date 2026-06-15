package com.nec.middleware.idGenerator.service;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.entity.IdGenerator;
import com.nec.middleware.idGenerator.repository.IdGeneratorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniqueIdGeneratorService {

    private final IdGeneratorRepository idGeneratorRepository;

    @Transactional
    public String generateId(
            ModuleCode moduleCode,
            String prefix) {

        IdGenerator generator =
                idGeneratorRepository
                        .findByModuleCode(moduleCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Module configuration not found"));
        Long nextNumber =
                generator.getLastNumber() + 1;

        generator.setLastNumber(nextNumber);

        idGeneratorRepository.save(generator);

        return prefix +String.format("%03d", nextNumber);
    }
}
