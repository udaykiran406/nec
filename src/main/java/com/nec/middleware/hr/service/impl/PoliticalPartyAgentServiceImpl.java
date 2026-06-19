package com.nec.middleware.hr.service.impl;

import com.nec.middleware.Lookups.entity.ThirdPartyStatus;
import com.nec.middleware.Lookups.repository.LookupGenderRepository;
import com.nec.middleware.Lookups.repository.ThirdPartyStatusRepository;
import com.nec.middleware.exception.DuplicateResourceException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.hr.constant.PoliticalPartyAgentConstants;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentFilterRequestDto;
import com.nec.middleware.hr.dto.request.PoliticalPartyAgentRequestDto;
import com.nec.middleware.hr.dto.response.PoliticalPartyAgentResponseDto;
import com.nec.middleware.hr.entity.PoliticalPartyAgent;
import com.nec.middleware.hr.mapper.PoliticalPartyAgentMapper;
import com.nec.middleware.hr.repository.PoliticalPartyAgentRepository;
import com.nec.middleware.hr.service.PoliticalPartyAgentService;
import com.nec.middleware.hr.specification.PoliticalPartyAgentSearchSpecification;
import com.nec.middleware.hr.util.FileStorageUtil;
import com.nec.middleware.idGenerator.Enum.ModuleCode;
import com.nec.middleware.idGenerator.service.UniqueIdGeneratorService;
import com.nec.middleware.masterdata.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class PoliticalPartyAgentServiceImpl implements PoliticalPartyAgentService {

    private final PoliticalPartyAgentRepository politicalPartyAgentRepository;
    private final PoliticalPartyAgentMapper politicalPartyAgentMapper;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final MasterDataRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final LookupGenderRepository genderRepository;
    private final PollingStationRepository pollingStationRepository;
    private final PoliticalPartyRepository politicalPartyRepository;
    private final ThirdPartyStatusRepository statusRepository;

    private final FileStorageUtil fileStorageUtil;

    private static final String PHOTO_SUB_FOLDER = "political-party-agents";
    // ------------------------------------------------------------------ SAVE / UPDATE

    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto savePartyAgent(PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto, MultipartFile photo) {

        validateAgent(politicalPartyAgentRequestDto);
        log.info("Creating political party agent. AgentName: {}",politicalPartyAgentRequestDto.getAgentName());
        PoliticalPartyAgent politicalPartyAgentEntity = politicalPartyAgentMapper.toPoliticalPartyAgentEntity(politicalPartyAgentRequestDto);

        resolveAndSetForeignKeys(politicalPartyAgentEntity, politicalPartyAgentRequestDto);

        // 4. Generate code — only after all lookups succeeded
        politicalPartyAgentEntity.setPoliticalPartyAgentUserId(
                generatePoliticalPartyAgentNumber()
        );

        // store photo AFTER portalUserId is generated
        String storedPhotoPath = fileStorageUtil.storePhoto(
                photo,
                PHOTO_SUB_FOLDER,
                politicalPartyAgentEntity.getPoliticalPartyAgentUserId()
        );
        politicalPartyAgentEntity.setPhotoPath(storedPhotoPath);
        log.info("Political party agent created: partyAgentUserId='{}'", politicalPartyAgentEntity.getPoliticalPartyAgentUserId());

        PoliticalPartyAgent politicalPartyAgent = politicalPartyAgentRepository.save(politicalPartyAgentEntity);
        return politicalPartyAgentMapper.politicalPartyResponseDto(politicalPartyAgent);
    }

    // ------------------------------------------------------------------ READ

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyAgentResponseDto getPolticalPartyAgentById(String agentUserId) {
        return politicalPartyAgentMapper.politicalPartyResponseDto(findByAgentUserId(agentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PoliticalPartyAgentResponseDto> getAllPartyAgents(PoliticalPartyAgentFilterRequestDto filterDto, int page, int size) {

        if (filterDto == null) {
            filterDto = new PoliticalPartyAgentFilterRequestDto();
        }
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return politicalPartyAgentRepository.findAll(
                        PoliticalPartyAgentSearchSpecification.buildSpecification(filterDto),
                        pageable)
                .map(politicalPartyAgentMapper::politicalPartyResponseDto);
    }

    // ------------------------------------------------------------------ SOFT DELETE

    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto changeStatus(String partyAgentId, Boolean isActiveFlag) {

        PoliticalPartyAgent partyAgent = findByAgentUserId(partyAgentId);
        partyAgent.setIsActive(isActiveFlag);
        log.info("Political party agent status changed with id: {}", partyAgent);
        return politicalPartyAgentMapper.politicalPartyResponseDto(
                politicalPartyAgentRepository.save(politicalPartyAgentRepository.save(partyAgent)));
    }
    //-------------------------------------------------------------Update
    @Override
    @Transactional
    public PoliticalPartyAgentResponseDto updatePoliticalPartyAgent(
            String agentUserId,
            PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto,MultipartFile photo) {
        log.info("Update political party agent request, agentUserId='{}'", agentUserId);

        PoliticalPartyAgent politicalPartyAgent =
                findByAgentUserId(agentUserId);

        validateAgentForUpdate(
                politicalPartyAgentRequestDto,
                politicalPartyAgent.getId()
        );

        // Only touch the photo if a new file was actually sent — otherwise
        // keep whatever is already on the entity.
        if (photo != null && !photo.isEmpty()) {
            fileStorageUtil.deleteIfExists(politicalPartyAgent.getPhotoPath());

            String newPhotoPath = fileStorageUtil.storePhoto(
                    photo,
                    PHOTO_SUB_FOLDER,
                    politicalPartyAgent.getPoliticalPartyAgentUserId()
            );

            politicalPartyAgentRequestDto.setPhotoPath(newPhotoPath);
        }

        updatePoliticalPartyAgents(
                politicalPartyAgent,
                politicalPartyAgentRequestDto
        );

        politicalPartyAgentRepository.save(politicalPartyAgent);
        log.info("Political party agent updated: partyAgentUserId='{}'", politicalPartyAgent.getPoliticalPartyAgentUserId());

        return politicalPartyAgentMapper.politicalPartyResponseDto(
                politicalPartyAgent
        );
    }
    // ------------------------------------------------------------------
    //Code Generation
    // ------------------------------------------------------------------
    public String generatePoliticalPartyAgentNumber() {
        String prefix= PoliticalPartyAgentConstants.CODE_PREFIX+"-"
                + Year.now().getValue()+"-";
        return uniqueIdGeneratorService.generateId(
                ModuleCode.POLITICAL_PARTY_AGENT,
                prefix
        );
    }
    // ------------------------------------------------------------------
    // Validation
    // ------------------------------------------------------------------

    public void validateAgent(PoliticalPartyAgentRequestDto politicalPartyAgentRequest) {

        if (politicalPartyAgentRepository.existsByEmail(politicalPartyAgentRequest.getEmail()) ||
                politicalPartyAgentRepository.existsByPhone(politicalPartyAgentRequest.getPhone())) {
            throw new DuplicateResourceException(PoliticalPartyAgentConstants.AGENT_ALREADY_EXISTS);
        }
    }

    private void validateAgentForUpdate(
            PoliticalPartyAgentRequestDto request,
            Long id) {

        if (politicalPartyAgentRepository.existsByEmailAndIdNot(
                request.getEmail(), id)
                ||
                politicalPartyAgentRepository.existsByPhoneAndIdNot(
                        request.getPhone(), id)) {

            throw new DuplicateResourceException(
                    PoliticalPartyAgentConstants.AGENT_ALREADY_EXISTS
            );
        }
    }


    private PoliticalPartyAgent findByAgentUserId(String agentUserId) {
        return politicalPartyAgentRepository.findByPoliticalPartyAgentUserId(agentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PoliticalPartyAgentConstants.AGENT_NOT_FOUND + agentUserId));
    }

    /**
     * Shared FK resolution used by both {@code savePartyAgent} and
     */
    public void resolveAndSetForeignKeys(
            PoliticalPartyAgent entity,
            PoliticalPartyAgentRequestDto dto) {
        log.debug("Resolving FKs for partyAgentUserId='{}'", entity.getPoliticalPartyAgentUserId());

        entity.setPollingStation(
                pollingStationRepository.findById(dto.getPollingStationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Polling Station not found with id: " + dto.getPollingStationId()))
        );

        entity.setPoliticalPartyName(
                politicalPartyRepository.findById(dto.getPoliticalPartyNameId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Political Party not found with id: " + dto.getPoliticalPartyNameId()))
        );

        entity.setGender(
                genderRepository.findById(dto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found with id: " + dto.getGenderId()))
        );

        entity.setRegion(
                regionRepository.findById(dto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found with id: " + dto.getRegionId()))
        );

        entity.setDistrict(
                districtRepository.findById(dto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found with id: " + dto.getDistrictId()))
        );

        entity.setCity(
                cityRepository.findById(dto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found with id: " + dto.getCityId()))
        );

        ThirdPartyStatus pendingStatus =
                statusRepository
                        .findByCode("PENDING")
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "PENDING status not configured"));

        entity.setStatus(pendingStatus);
    }

    public void updatePoliticalPartyAgents(
            PoliticalPartyAgent politicalPartyAgent,
            PoliticalPartyAgentRequestDto politicalPartyAgentRequestDto) {

        politicalPartyAgent.setAgentName(politicalPartyAgentRequestDto.getAgentName());

        politicalPartyAgent.setPhone(politicalPartyAgentRequestDto.getPhone());

        politicalPartyAgent.setEmail(politicalPartyAgentRequestDto.getEmail());

        politicalPartyAgent.setUpdatedBy(politicalPartyAgentRequestDto.getUpdatedBy());


        // Political Party

        politicalPartyAgent.setPoliticalPartyName(
                politicalPartyRepository.findById(politicalPartyAgentRequestDto.getPoliticalPartyNameId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Political Party not found"))
        );

        // Gender
        politicalPartyAgent.setGender(
                genderRepository.findById(politicalPartyAgentRequestDto.getGenderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Gender not found"))
        );

        // Polling Station
        politicalPartyAgent.setPollingStation(
                pollingStationRepository.findById(politicalPartyAgentRequestDto.getPollingStationId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Polling Station not found"))
        );

        // Region
        politicalPartyAgent.setRegion(
                regionRepository.findById(politicalPartyAgentRequestDto.getRegionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Region not found"))
        );

        // District
        politicalPartyAgent.setDistrict(
                districtRepository.findById(politicalPartyAgentRequestDto.getDistrictId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("District not found"))
        );

        // City
        politicalPartyAgent.setCity(
                cityRepository.findById(politicalPartyAgentRequestDto.getCityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("City not found"))
        );
    }
}