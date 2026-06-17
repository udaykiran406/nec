package com.nec.middleware.hr.service.impl;

import com.nec.middleware.hr.dto.request.UniversityTraineeRequestDto;
import com.nec.middleware.hr.dto.response.UniversityTraineeResponseDto;
import com.nec.middleware.hr.entity.UniversityTrainee;
import com.nec.middleware.hr.mapper.UniversityTraineeMapper;
import com.nec.middleware.hr.repository.UniversityTraineeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Owns the per-row transaction boundary for bulk-uploaded University
 * Trainees. This class is deliberately thin — it does NOT duplicate
 * validation, FK resolution, or ID generation. That logic lives in exactly
 * one place: the package-private helper methods on
 * {@link UniversityTraineeServiceImpl} ({@code validateTrainee},
 * {@code resolveAndSetForeignKeys}, {@code generateUniversityTraineeNumber}).
 *
 * <h3>Why this class exists at all</h3>
 * In a bulk upload, each row must succeed or fail <b>independently</b> —
 * one bad row (e.g. an unresolvable FK) must NOT roll back rows that were
 * already saved earlier in the same loop.
 *
 * <p>Spring's {@code @Transactional} is implemented via an AOP proxy. The
 * proxy only intercepts calls that arrive from <b>outside</b> the bean —
 * a method call from one method to another <i>within the same class</i>
 * (e.g. {@code this.persistSingleRow(...)}) bypasses the proxy entirely,
 * so {@code @Transactional} on that method would be silently ignored.
 * That's why {@code persistSingleRow} can't simply live inside
 * {@code UniversityTraineeServiceImpl} itself — it needs to be on a
 * separate bean so the call from {@code bulkCreate()}'s loop goes through
 * a real proxy boundary, and {@code Propagation.REQUIRES_NEW} below then
 * guarantees each row gets its own fresh transaction that commits or rolls
 * back independently of every other row.
 *
 * <h3>Why {@code @Lazy} on the {@code ServiceImpl} reference</h3>
 * {@code UniversityTraineeServiceImpl} already depends on this class (its
 * {@code bulkCreate()} loop calls {@code persistSingleRow}). If this class
 * also took a normal constructor dependency on
 * {@code UniversityTraineeServiceImpl}, Spring would have two beans each
 * requiring the other to exist first — a circular dependency it cannot
 * resolve, failing at startup with {@code BeanCurrentlyInCreationException}.
 *
 * <p>{@code @Lazy} tells Spring to inject a proxy placeholder here instead
 * of the real bean immediately. Both beans finish constructing normally,
 * and the real {@code UniversityTraineeServiceImpl} instance is only
 * resolved the first time a method is actually invoked on it (i.e. inside
 * {@link #persistSingleRow}, well after application startup). This breaks
 * the cycle while still letting validation/FK-resolution/ID-generation
 * live in exactly one place rather than being duplicated here.
 */
@Slf4j
@Service
public class UniversityTraineeRowPersister {

    private final UniversityTraineeRepository universityTraineerepository;
    private final UniversityTraineeMapper     universityTraineeMapper;
    private final UniversityTraineeServiceImpl serviceImpl;

    @Autowired
    public UniversityTraineeRowPersister(
            UniversityTraineeRepository universityTraineerepository,
            UniversityTraineeMapper universityTraineeMapper,
            @Lazy UniversityTraineeServiceImpl serviceImpl) {
        this.universityTraineerepository = universityTraineerepository;
        this.universityTraineeMapper = universityTraineeMapper;
        this.serviceImpl = serviceImpl;
    }

    /**
     * Validate, build entity, resolve FKs, and save a single row in its own
     * independent transaction.
     *
     * <p>No photo is stored — bulk-created trainees always have a null photoPath.
     *
     * @throws com.nec.middleware.exception.DuplicateResourceException if email/phone already exists
     * @throws com.nec.middleware.exception.ResourceNotFoundException  if any referenced FK id does not exist
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UniversityTraineeResponseDto persistSingleRow(UniversityTraineeRequestDto dto) {

        // Delegates to the single shared implementation of each rule on
        // ServiceImpl — see class javadoc for why this is @Lazy.
        serviceImpl.validateTrainee(dto);

        UniversityTrainee entity = universityTraineeMapper.toEntity(dto);
        serviceImpl.resolveAndSetForeignKeys(entity, dto);
        entity.setUniversityTraineeId(serviceImpl.generateUniversityTraineeNumber());
        // photoPath intentionally left null in bulk mode

        return universityTraineeMapper.toResponseDto(universityTraineerepository.save(entity));
    }
}
