package com.nec.middleware.hr.service.impl;

import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.hr.constant.PoliticalPartyAgentConstants;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentListRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.entity.PoliticalPartyAgent;
import com.nec.middleware.hr.mapper.PoliticalPartyAgentMapper;
import com.nec.middleware.hr.repository.PoliticalPartyAgentRepository;
import com.nec.middleware.hr.service.PoliticalPartyAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoliticalPartyAgentServiceImpl implements PoliticalPartyAgentService {

    private static final String CODE_PREFIX = "PA";
    private static final int    CODE_PAD    = 3;   // PA001, PA002 …

    private final PoliticalPartyAgentRepository repository;
    private final PoliticalPartyAgentMapper      mapper;

    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto saveOrUpdate(PoliticalPartyAgentRequestDto requestDto) {

        validateAgent(requestDto);
        PoliticalPartyAgent entity;

        if (requestDto.getId() == null) {

            log.info("Creating political party agent");
            entity = mapper.toEntity(requestDto);
            entity.setCode(generateCode());

        } else {

            log.info("Updating political party agent id: {}", requestDto.getId());
            entity = findByIdAndNotDeleted(requestDto.getId());
            mapper.updateEntity(entity, requestDto);
            // code is never changed on update
        }

        PoliticalPartyAgent saved = repository.save(entity);
        return mapper.toResponseDto(saved);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyAgentResponseDto getById(Long id) {
        return mapper.toResponseDto(findByIdAndNotDeleted(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PoliticalPartyAgentResponseDto> getAll(PoliticalPartyAgentListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAllWithFilters(
                filterDto.getPoliticalPartyNameId(),
                filterDto.getPollingStationId(),
                filterDto.getRegionId(),
                filterDto.getDistrictId(),
                filterDto.getCityId(),
                filterDto.getStatusId(),
                filterDto.getIsActive(),
                pageable
        ).map(mapper::toResponseDto);
    }

    // ------------------------------------------------------------------ STATUS CHANGE

    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto changeStatus(Long id) {

        PoliticalPartyAgent entity = findById(id);
        boolean newStatus = !entity.getIsActive();
        entity.setIsActive(newStatus);

        log.info("Changing status for political party agent id: {} → isActive={}", id, newStatus);

        return mapper.toResponseDto(repository.save(entity));
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public void softDelete(Long id) {

        PoliticalPartyAgent entity = findById(id);
        entity.setIsActive(false);

        repository.save(entity);
        log.info("Political party agent soft deleted with id: {}", id);
    }

    // ------------------------------------------------------------------
    // Code generation
    // ------------------------------------------------------------------

    private String generateCode() {
        int next = repository.findMaxCodeSequence() + 1;
        return CODE_PREFIX + String.format("%0" + CODE_PAD + "d", next);
    }

    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    private void validateAgent(PoliticalPartyAgentRequestDto dto) {

        if (dto.getId() == null) {

            if (repository.existsByEmailAndIsActiveTrue(dto.getEmail()) ||
                    repository.existsByPhoneAndIsActiveTrue(dto.getPhone())) {
                throw new ValidationException(PoliticalPartyAgentConstants.AGENT_ALREADY_EXISTS);
            }

        } else {

            if (repository.existsByEmailAndIsActiveTrueAndIdNot(dto.getEmail(), dto.getId()) ||
                    repository.existsByPhoneAndIsActiveTrueAndIdNot(dto.getPhone(), dto.getId())) {
                throw new ValidationException(PoliticalPartyAgentConstants.AGENT_ALREADY_EXISTS);
            }
        }
    }

    // ------------------------------------------------------------------
    // Private Helpers
    // ------------------------------------------------------------------

    private PoliticalPartyAgent findByIdAndNotDeleted(Long id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PoliticalPartyAgentConstants.AGENT_NOT_FOUND + id));
    }

    private PoliticalPartyAgent findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PoliticalPartyAgentConstants.AGENT_NOT_FOUND + id));
    }
}
