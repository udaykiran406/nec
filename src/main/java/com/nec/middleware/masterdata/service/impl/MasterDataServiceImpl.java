package com.nec.middleware.masterdata.service.impl;


import com.nec.middleware.exception.DuplicateException;
import com.nec.middleware.exception.ResourceNotFoundException;
import com.nec.middleware.masterdata.constants.MasterDataConstants;
import com.nec.middleware.masterdata.dto.request.*;
import com.nec.middleware.masterdata.dto.response.CityResponse;
import com.nec.middleware.masterdata.dto.response.DistrictResponse;
import com.nec.middleware.masterdata.dto.response.MasterDataResponse;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.entity.MasterDataRegion;

import com.nec.middleware.masterdata.mapper.MasterDataMapper;
import com.nec.middleware.masterdata.repository.CityRepository;
import com.nec.middleware.masterdata.repository.DistrictRepository;
import com.nec.middleware.masterdata.repository.MasterDataRepository;
import com.nec.middleware.masterdata.dto.response.PollingStationResponse;
import com.nec.middleware.masterdata.entity.MasterDataPollingStation;
import com.nec.middleware.masterdata.repository.PollingStationRepository;
import com.nec.middleware.masterdata.dto.response.VoterRegistrationCenterResponse;
import com.nec.middleware.masterdata.entity.MasterDataVoterRegistrationCenter;
import com.nec.middleware.masterdata.repository.VoterRegistrationCenterRepository;
import com.nec.middleware.masterdata.dto.response.UniversityResponse;
import com.nec.middleware.masterdata.entity.MasterDataUniversity;
import com.nec.middleware.masterdata.repository.UniversityRepository;
import com.nec.middleware.masterdata.service.MasterDataService;
import com.nec.middleware.masterdata.dto.response.WarehouseResponse;
import com.nec.middleware.masterdata.entity.MasterDataWarehouse;
import com.nec.middleware.masterdata.repository.WarehouseRepository;
import com.nec.middleware.masterdata.dto.response.WarehouseSubStoreResponse;
import com.nec.middleware.masterdata.entity.MasterDataWarehouseSubStore;
import com.nec.middleware.masterdata.repository.WarehouseSubStoreRepository;
import com.nec.middleware.masterdata.dto.response.PoliticalPartyResponse;
import com.nec.middleware.masterdata.entity.MasterDataPoliticalParty;
import com.nec.middleware.masterdata.repository.PoliticalPartyRepository;
import com.nec.middleware.masterdata.dto.response.BankAccountResponse;
import com.nec.middleware.masterdata.entity.MasterDataBankAccount;
import com.nec.middleware.masterdata.repository.BankAccountRepository;
import com.nec.middleware.masterdata.dto.response.DeploymentRoleResponse;
import com.nec.middleware.masterdata.entity.MasterDataDeploymentRole;
import com.nec.middleware.masterdata.repository.DeploymentRoleRepository;
import com.nec.middleware.masterdata.dto.response.HrTrainerTotResponse;
import com.nec.middleware.masterdata.entity.MasterDataHrTrainerTot;
import com.nec.middleware.masterdata.repository.HrTrainerTotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final MasterDataRepository masterDataRepository;
    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;
    private final MasterDataMapper masterDataMapper;
    private final PollingStationRepository pollingStationRepository;
    private final VoterRegistrationCenterRepository voterRegistrationCenterRepository;
    private final UniversityRepository universityRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseSubStoreRepository warehouseSubStoreRepository;
    private final PoliticalPartyRepository politicalPartyRepository;
    private final BankAccountRepository bankAccountRepository;
    private final DeploymentRoleRepository deploymentRoleRepository;
    private final HrTrainerTotRepository hrTrainerTotRepository;

    // =========================================================================
    // REGION METHODS
    // =========================================================================

    @Override
    @Transactional
    public MasterDataResponse saveRegion(MasterDataRequest request) {
        if (request.getId() == null) {
            return createRegion(request);
        }
        return updateRegion(request);
    }

    private MasterDataResponse createRegion(MasterDataRequest request) {
        boolean exists = masterDataRepository.existsByRegionNameIgnoreCaseAndIsDeleted(
                request.getRegionName().trim(),
                MasterDataConstants.IS_DELETED_FALSE
        );

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_REGION_NAME + request.getRegionName()
            );
        }

        MasterDataRegion entity = masterDataMapper.toEntity(request);
        MasterDataRegion saved = masterDataRepository.save(entity);

        return masterDataMapper.toResponseDto(saved);
    }

    private MasterDataResponse updateRegion(MasterDataRequest request) {
        MasterDataRegion entity = masterDataRepository
                .findByIdAndIsDeleted(request.getId(), MasterDataConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MasterDataConstants.RECORD_NOT_FOUND + request.getId()
                ));

        boolean exists = masterDataRepository.existsByRegionNameIgnoreCaseAndIsDeletedAndIdNot(
                request.getRegionName().trim(),
                MasterDataConstants.IS_DELETED_FALSE,
                request.getId()
        );

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_REGION_NAME + request.getRegionName()
            );
        }

        masterDataMapper.updateEntity(entity, request);
        MasterDataRegion updated = masterDataRepository.save(entity);

        return masterDataMapper.toResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MasterDataResponse getRegionById(Long id) {
        MasterDataRegion entity = masterDataRepository
                .findByIdAndIsDeleted(id, MasterDataConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MasterDataConstants.RECORD_NOT_FOUND + id
                ));

        return masterDataMapper.toResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MasterDataResponse> getAllRegions() {
        return masterDataRepository
                .findAllByIsDeletedOrderByRegionNameAsc(
                        MasterDataConstants.IS_DELETED_FALSE)
                .stream()
                .map(masterDataMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // DISTRICT METHODS
    // =========================================================================

    @Override
    @Transactional
    public DistrictResponse saveDistrict(DistrictRequest request) {
        if (request.getId() == null) {
            return createDistrict(request);
        }
        return updateDistrict(request);
    }

    private DistrictResponse createDistrict(DistrictRequest request) {
        boolean exists = districtRepository.existsByDistrictNameIgnoreCaseAndRegionIdAndIsDeleted(
                request.getDistrictName().trim(),
                request.getRegionId(),
                MasterDataConstants.IS_DELETED_FALSE
        );

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_DISTRICT_NAME + request.getDistrictName()
            );
        }

        MasterDataDistrict entity = masterDataMapper.toDistrictEntity(request);
        MasterDataDistrict saved = districtRepository.save(entity);

        return masterDataMapper.toDistrictResponseDto(saved);
    }

    private DistrictResponse updateDistrict(DistrictRequest request) {
        MasterDataDistrict entity = districtRepository
                .findByIdAndIsDeleted(request.getId(), MasterDataConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MasterDataConstants.RECORD_NOT_FOUND + request.getId()
                ));

        boolean exists = districtRepository.existsByDistrictNameIgnoreCaseAndRegionIdAndIsDeletedAndIdNot(
                request.getDistrictName().trim(),
                request.getRegionId(),
                MasterDataConstants.IS_DELETED_FALSE,
                request.getId()
        );

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_DISTRICT_NAME + request.getDistrictName()
            );
        }

        masterDataMapper.updateDistrictEntity(entity, request);
        MasterDataDistrict updated = districtRepository.save(entity);

        return masterDataMapper.toDistrictResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictResponse getDistrictById(Long id) {
        MasterDataDistrict entity = districtRepository
                .findByIdAndIsDeleted(id, MasterDataConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MasterDataConstants.RECORD_NOT_FOUND + id
                ));

        return masterDataMapper.toDistrictResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getAllDistricts(DistrictListRequestDto request) {

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        return districtRepository.findDistricts(
                        request.getRegionId(),
                        pageable)
                .map(masterDataMapper::toDistrictResponseDto);
    }

    // =========================================================================
    // CITY METHODS
    // =========================================================================

    @Override
    @Transactional
    public CityResponse saveCity(CityRequest request) {
        if (request.getId() == null) {
            return createCity(request);
        }
        return updateCity(request);
    }

    private CityResponse createCity(CityRequest request) {
        boolean exists = cityRepository.existsByCityNameIgnoreCaseAndDistrictIdAndIsDeleted(
                request.getCityName().trim(),
                request.getDistrictId(),
                MasterDataConstants.IS_DELETED_FALSE
        );

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_CITY_NAME + request.getCityName()
            );
        }

        MasterDataCity entity = masterDataMapper.toCityEntity(request);
        MasterDataCity saved = cityRepository.save(entity);

        return masterDataMapper.toCityResponseDto(saved);
    }

    private CityResponse updateCity(CityRequest request) {
        MasterDataCity entity = cityRepository
                .findByIdAndIsDeleted(request.getId(), MasterDataConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MasterDataConstants.RECORD_NOT_FOUND + request.getId()
                ));

        boolean exists = cityRepository.existsByCityNameIgnoreCaseAndDistrictIdAndIsDeletedAndIdNot(
                request.getCityName().trim(),
                request.getDistrictId(),
                MasterDataConstants.IS_DELETED_FALSE,
                request.getId()
        );

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_CITY_NAME + request.getCityName()
            );
        }

        masterDataMapper.updateCityEntity(entity, request);
        MasterDataCity updated = cityRepository.save(entity);

        return masterDataMapper.toCityResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CityResponse getCityById(Long id) {
        MasterDataCity entity = cityRepository
                .findByIdAndIsDeleted(id, MasterDataConstants.IS_DELETED_FALSE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MasterDataConstants.RECORD_NOT_FOUND + id
                ));

        return masterDataMapper.toCityResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CityResponse> getAllCities(CityListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize()
        );

        return cityRepository.findCities(
                        filterDto.getRegionId(),
                        filterDto.getDistrictId(),
                        pageable
                )
                .map(masterDataMapper::toCityResponseDto);
    }
    // =========================================================================
// POLLING STATION METHODS
// =========================================================================

    @Override
    @Transactional
    public PollingStationResponse savePollingStation(
            PollingStationRequest request) {

        if (request.getId() == null) {
            return createPollingStation(request);
        }

        return updatePollingStation(request);
    }

    private PollingStationResponse createPollingStation(
            PollingStationRequest request) {

        boolean exists =
                pollingStationRepository
                        .existsByPollingStationCodeIgnoreCaseAndIsDeleted(
                                request.getPollingStationCode(),
                                MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_POLLING_STATION_CODE
                            + request.getPollingStationCode());
        }

        MasterDataPollingStation entity =
                masterDataMapper.toPollingStationEntity(request);

        MasterDataPollingStation saved =
                pollingStationRepository.save(entity);

        return masterDataMapper.toPollingStationResponseDto(saved);
    }

    private PollingStationResponse updatePollingStation(
            PollingStationRequest request) {

        MasterDataPollingStation entity =
                pollingStationRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                pollingStationRepository
                        .existsByPollingStationCodeIgnoreCaseAndIsDeletedAndIdNot(
                                request.getPollingStationCode(),
                                MasterDataConstants.IS_DELETED_FALSE,
                                request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_POLLING_STATION_CODE
                            + request.getPollingStationCode());
        }

        masterDataMapper.updatePollingStationEntity(entity, request);

        MasterDataPollingStation updated =
                pollingStationRepository.save(entity);

        return masterDataMapper.toPollingStationResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PollingStationResponse getPollingStationById(Long id) {

        MasterDataPollingStation entity =
                pollingStationRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toPollingStationResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PollingStationResponse> getAllPollingStations(
            PollingStationListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize()
        );

        return pollingStationRepository.findPollingStations(
                        filterDto.getRegionId(),
                        filterDto.getDistrictId(),
                        filterDto.getCityId(),
                        pageable
                )
                .map(masterDataMapper::toPollingStationResponseDto);
    }

    // =========================================================================
// VOTER REGISTRATION CENTER METHODS
// =========================================================================

    @Override
    @Transactional
    public VoterRegistrationCenterResponse saveVoterRegistrationCenter(
            VoterRegistrationCenterRequest request) {

        if (request.getId() == null) {
            return createVoterRegistrationCenter(request);
        }

        return updateVoterRegistrationCenter(request);
    }

    private VoterRegistrationCenterResponse createVoterRegistrationCenter(
            VoterRegistrationCenterRequest request) {

        boolean exists =
                voterRegistrationCenterRepository
                        .existsByVrcCodeIgnoreCaseAndIsDeleted(
                                request.getVrcCode(),
                                MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_VRC_CODE
                            + request.getVrcCode());
        }

        MasterDataVoterRegistrationCenter entity =
                masterDataMapper.toVoterRegistrationCenterEntity(request);

        MasterDataVoterRegistrationCenter saved =
                voterRegistrationCenterRepository.save(entity);

        return masterDataMapper.toVoterRegistrationCenterResponseDto(saved);
    }

    private VoterRegistrationCenterResponse updateVoterRegistrationCenter(
            VoterRegistrationCenterRequest request) {

        MasterDataVoterRegistrationCenter entity =
                voterRegistrationCenterRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                voterRegistrationCenterRepository
                        .existsByVrcCodeIgnoreCaseAndIsDeletedAndIdNot(
                                request.getVrcCode(),
                                MasterDataConstants.IS_DELETED_FALSE,
                                request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_VRC_CODE
                            + request.getVrcCode());
        }

        masterDataMapper.updateVoterRegistrationCenterEntity(entity, request);

        MasterDataVoterRegistrationCenter updated =
                voterRegistrationCenterRepository.save(entity);

        return masterDataMapper.toVoterRegistrationCenterResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public VoterRegistrationCenterResponse getVoterRegistrationCenterById(Long id) {

        MasterDataVoterRegistrationCenter entity =
                voterRegistrationCenterRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toVoterRegistrationCenterResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoterRegistrationCenterResponse> getAllVoterRegistrationCenters(
            VoterRegistrationCenterListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize()
        );

        return voterRegistrationCenterRepository.findVoterRegistrationCenters(
                        filterDto.getRegionId(),
                        filterDto.getDistrictId(),
                        filterDto.getCityId(),
                        pageable
                )
                .map(masterDataMapper::toVoterRegistrationCenterResponseDto);
    }

    // =========================================================================
// UNIVERSITY METHODS
// =========================================================================

    @Override
    @Transactional
    public UniversityResponse saveUniversity(UniversityRequest request) {

        if (request.getId() == null) {
            return createUniversity(request);
        }

        return updateUniversity(request);
    }

    private UniversityResponse createUniversity(UniversityRequest request) {

        boolean exists =
                universityRepository
                        .existsByUniversityNameIgnoreCaseAndIsDeleted(
                                request.getUniversityName(),
                                MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_UNIVERSITY_NAME
                            + request.getUniversityName());
        }

        MasterDataUniversity entity =
                masterDataMapper.toUniversityEntity(request);

        MasterDataUniversity saved =
                universityRepository.save(entity);

        return masterDataMapper.toUniversityResponseDto(saved);
    }

    private UniversityResponse updateUniversity(UniversityRequest request) {

        MasterDataUniversity entity =
                universityRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                universityRepository
                        .existsByUniversityNameIgnoreCaseAndIsDeletedAndIdNot(
                                request.getUniversityName(),
                                MasterDataConstants.IS_DELETED_FALSE,
                                request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_UNIVERSITY_NAME
                            + request.getUniversityName());
        }

        masterDataMapper.updateUniversityEntity(entity, request);

        MasterDataUniversity updated =
                universityRepository.save(entity);

        return masterDataMapper.toUniversityResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UniversityResponse getUniversityById(Long id) {

        MasterDataUniversity entity =
                universityRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toUniversityResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniversityResponse> getAllUniversities() {

        return universityRepository
                .findAllByIsDeletedOrderByUniversityNameAsc(
                        MasterDataConstants.IS_DELETED_FALSE)
                .stream()
                .map(masterDataMapper::toUniversityResponseDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
// WAREHOUSE METHODS
// =========================================================================

    @Override
    @Transactional
    public WarehouseResponse saveWarehouse(WarehouseRequest request) {

        if (request.getId() == null) {
            return createWarehouse(request);
        }

        return updateWarehouse(request);
    }

    private WarehouseResponse createWarehouse(WarehouseRequest request) {

        boolean exists =
                warehouseRepository.existsByWarehouseNameIgnoreCaseAndIsDeleted(
                        request.getWarehouseName(),
                        MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_WAREHOUSE_NAME
                            + request.getWarehouseName());
        }

        MasterDataWarehouse entity =
                masterDataMapper.toWarehouseEntity(request);

        MasterDataWarehouse saved =
                warehouseRepository.save(entity);

        return masterDataMapper.toWarehouseResponseDto(saved);
    }

    private WarehouseResponse updateWarehouse(WarehouseRequest request) {

        MasterDataWarehouse entity =
                warehouseRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                warehouseRepository.existsByWarehouseNameIgnoreCaseAndIsDeletedAndIdNot(
                        request.getWarehouseName(),
                        MasterDataConstants.IS_DELETED_FALSE,
                        request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_WAREHOUSE_NAME
                            + request.getWarehouseName());
        }

        masterDataMapper.updateWarehouseEntity(entity, request);

        MasterDataWarehouse updated =
                warehouseRepository.save(entity);

        return masterDataMapper.toWarehouseResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {

        MasterDataWarehouse entity =
                warehouseRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toWarehouseResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseResponse> getAllWarehouses(
            WarehouseListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize()
        );

        return warehouseRepository.findWarehouses(
                        filterDto.getRegionId(),
                        filterDto.getDistrictId(),
                        filterDto.getCityId(),
                        pageable
                )
                .map(masterDataMapper::toWarehouseResponseDto);
    }

    // =========================================================================
// WAREHOUSE SUB STORE METHODS
// =========================================================================

    @Override
    @Transactional
    public WarehouseSubStoreResponse saveWarehouseSubStore(
            WarehouseSubStoreRequest request) {

        if (request.getId() == null) {
            return createWarehouseSubStore(request);
        }

        return updateWarehouseSubStore(request);
    }

    private WarehouseSubStoreResponse createWarehouseSubStore(
            WarehouseSubStoreRequest request) {

        boolean exists =
                warehouseSubStoreRepository
                        .existsBySubStoreNameIgnoreCaseAndWarehouseIdAndIsDeleted(
                                request.getSubStoreName(),
                                request.getWarehouseId(),
                                MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_WAREHOUSE_SUB_STORE_NAME
                            + request.getSubStoreName());
        }

        MasterDataWarehouseSubStore entity =
                masterDataMapper.toWarehouseSubStoreEntity(request);

        MasterDataWarehouseSubStore saved =
                warehouseSubStoreRepository.save(entity);

        return masterDataMapper.toWarehouseSubStoreResponseDto(saved);
    }

    private WarehouseSubStoreResponse updateWarehouseSubStore(
            WarehouseSubStoreRequest request) {

        MasterDataWarehouseSubStore entity =
                warehouseSubStoreRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                warehouseSubStoreRepository
                        .existsBySubStoreNameIgnoreCaseAndWarehouseIdAndIsDeletedAndIdNot(
                                request.getSubStoreName(),
                                request.getWarehouseId(),
                                MasterDataConstants.IS_DELETED_FALSE,
                                request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_WAREHOUSE_SUB_STORE_NAME
                            + request.getSubStoreName());
        }

        masterDataMapper.updateWarehouseSubStoreEntity(entity, request);

        MasterDataWarehouseSubStore updated =
                warehouseSubStoreRepository.save(entity);

        return masterDataMapper.toWarehouseSubStoreResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseSubStoreResponse getWarehouseSubStoreById(Long id) {

        MasterDataWarehouseSubStore entity =
                warehouseSubStoreRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toWarehouseSubStoreResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseSubStoreResponse> getAllWarehouseSubStores(
            WarehouseSubStoreListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize()
        );

        return warehouseSubStoreRepository.findWarehouseSubStores(
                        filterDto.getWarehouseId(),
                        filterDto.getRegionId(),
                        filterDto.getDistrictId(),
                        filterDto.getCityId(),
                        pageable
                )
                .map(masterDataMapper::toWarehouseSubStoreResponseDto);
    }

    // =========================================================================
// POLITICAL PARTY METHODS
// =========================================================================

    @Override
    @Transactional
    public PoliticalPartyResponse savePoliticalParty(
            PoliticalPartyRequest request) {

        if (request.getId() == null) {
            return createPoliticalParty(request);
        }

        return updatePoliticalParty(request);
    }

    private PoliticalPartyResponse createPoliticalParty(
            PoliticalPartyRequest request) {

        boolean exists =
                politicalPartyRepository.existsByPartyNameIgnoreCaseAndIsDeleted(
                        request.getPartyName(),
                        MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_POLITICAL_PARTY_NAME
                            + request.getPartyName());
        }

        MasterDataPoliticalParty entity =
                masterDataMapper.toPoliticalPartyEntity(request);

        MasterDataPoliticalParty saved =
                politicalPartyRepository.save(entity);

        return masterDataMapper.toPoliticalPartyResponseDto(saved);
    }

    private PoliticalPartyResponse updatePoliticalParty(
            PoliticalPartyRequest request) {

        MasterDataPoliticalParty entity =
                politicalPartyRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                politicalPartyRepository.existsByPartyNameIgnoreCaseAndIsDeletedAndIdNot(
                        request.getPartyName(),
                        MasterDataConstants.IS_DELETED_FALSE,
                        request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_POLITICAL_PARTY_NAME
                            + request.getPartyName());
        }

        masterDataMapper.updatePoliticalPartyEntity(entity, request);

        MasterDataPoliticalParty updated =
                politicalPartyRepository.save(entity);

        return masterDataMapper.toPoliticalPartyResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyResponse getPoliticalPartyById(Long id) {

        MasterDataPoliticalParty entity =
                politicalPartyRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toPoliticalPartyResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoliticalPartyResponse> getAllPoliticalParties() {

        return politicalPartyRepository
                .findAllByIsDeletedOrderByPartyNameAsc(
                        MasterDataConstants.IS_DELETED_FALSE)
                .stream()
                .map(masterDataMapper::toPoliticalPartyResponseDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
// BANK ACCOUNT METHODS
// =========================================================================

    @Override
    @Transactional
    public BankAccountResponse saveBankAccount(
            BankAccountRequest request) {

        if (request.getId() == null) {
            return createBankAccount(request);
        }

        return updateBankAccount(request);
    }

    private BankAccountResponse createBankAccount(
            BankAccountRequest request) {

        boolean exists =
                bankAccountRepository.existsByAccountNumberAndIsDeleted(
                        request.getAccountNumber(),
                        MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_BANK_ACCOUNT_NUMBER
                            + request.getAccountNumber());
        }

        MasterDataBankAccount entity =
                masterDataMapper.toBankAccountEntity(request);

        MasterDataBankAccount saved =
                bankAccountRepository.save(entity);

        return masterDataMapper.toBankAccountResponseDto(saved);
    }

    private BankAccountResponse updateBankAccount(
            BankAccountRequest request) {

        MasterDataBankAccount entity =
                bankAccountRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                bankAccountRepository.existsByAccountNumberAndIsDeletedAndIdNot(
                        request.getAccountNumber(),
                        MasterDataConstants.IS_DELETED_FALSE,
                        request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_BANK_ACCOUNT_NUMBER
                            + request.getAccountNumber());
        }

        masterDataMapper.updateBankAccountEntity(entity, request);

        MasterDataBankAccount updated =
                bankAccountRepository.save(entity);

        return masterDataMapper.toBankAccountResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponse getBankAccountById(Long id) {

        MasterDataBankAccount entity =
                bankAccountRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toBankAccountResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BankAccountResponse> getAllBankAccounts(
            BankAccountListRequestDto filterDto) {

        Pageable pageable = PageRequest.of(
                filterDto.getPage(),
                filterDto.getSize()
        );

        return bankAccountRepository.findBankAccounts(
                        filterDto.getAccountType(),
                        filterDto.getAccountHolderName(),
                        filterDto.getBankName(),
                        filterDto.getBranchName(),
                        pageable
                )
                .map(masterDataMapper::toBankAccountResponseDto);
    }

    // =========================================================================
// DEPLOYMENT ROLE METHODS
// =========================================================================

    @Override
    @Transactional
    public DeploymentRoleResponse saveDeploymentRole(
            DeploymentRoleRequest request) {

        if (request.getId() == null) {
            return createDeploymentRole(request);
        }

        return updateDeploymentRole(request);
    }

    private DeploymentRoleResponse createDeploymentRole(
            DeploymentRoleRequest request) {

        boolean exists =
                deploymentRoleRepository.existsByRoleNameIgnoreCaseAndIsDeleted(
                        request.getRoleName(),
                        MasterDataConstants.IS_DELETED_FALSE);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_DEPLOYMENT_ROLE_NAME
                            + request.getRoleName());
        }

        MasterDataDeploymentRole entity =
                masterDataMapper.toDeploymentRoleEntity(request);

        MasterDataDeploymentRole saved =
                deploymentRoleRepository.save(entity);

        return masterDataMapper.toDeploymentRoleResponseDto(saved);
    }

    private DeploymentRoleResponse updateDeploymentRole(
            DeploymentRoleRequest request) {

        MasterDataDeploymentRole entity =
                deploymentRoleRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                deploymentRoleRepository.existsByRoleNameIgnoreCaseAndIsDeletedAndIdNot(
                        request.getRoleName(),
                        MasterDataConstants.IS_DELETED_FALSE,
                        request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_DEPLOYMENT_ROLE_NAME
                            + request.getRoleName());
        }

        masterDataMapper.updateDeploymentRoleEntity(entity, request);

        MasterDataDeploymentRole updated =
                deploymentRoleRepository.save(entity);

        return masterDataMapper.toDeploymentRoleResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DeploymentRoleResponse getDeploymentRoleById(Long id) {

        MasterDataDeploymentRole entity =
                deploymentRoleRepository
                        .findByIdAndIsDeleted(
                                id,
                                MasterDataConstants.IS_DELETED_FALSE)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toDeploymentRoleResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeploymentRoleResponse> getAllDeploymentRoles() {

        return deploymentRoleRepository
                .findAllByIsDeletedOrderByRoleNameAsc(
                        MasterDataConstants.IS_DELETED_FALSE)
                .stream()
                .map(masterDataMapper::toDeploymentRoleResponseDto)
                .collect(Collectors.toList());
    }


    // =========================================================================
// HR TRAINER TOT METHODS
// =========================================================================

    @Override
    @Transactional
    public HrTrainerTotResponse saveHrTrainerTot(
            HrTrainerTotRequest request) {

        if (request.getId() == null) {
            return createHrTrainerTot(request);
        }

        return updateHrTrainerTot(request);
    }

    private HrTrainerTotResponse createHrTrainerTot(
            HrTrainerTotRequest request) {

        boolean exists =
                hrTrainerTotRepository.existsByCodeIgnoreCaseAndIsDeleted(
                        request.getCode(),
                        false);

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_TRAINER_TOT_CODE
                            + request.getCode());
        }

        MasterDataHrTrainerTot entity =
                masterDataMapper.toHrTrainerTotEntity(request);

        MasterDataHrTrainerTot saved =
                hrTrainerTotRepository.save(entity);

        return masterDataMapper.toHrTrainerTotResponseDto(saved);
    }

    private HrTrainerTotResponse updateHrTrainerTot(
            HrTrainerTotRequest request) {

        MasterDataHrTrainerTot entity =
                hrTrainerTotRepository
                        .findByIdAndIsDeleted(
                                request.getId(),
                                false)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + request.getId()));

        boolean exists =
                hrTrainerTotRepository.existsByCodeIgnoreCaseAndIsDeletedAndIdNot(
                        request.getCode(),
                        false,
                        request.getId());

        if (exists) {
            throw new DuplicateException(
                    MasterDataConstants.DUPLICATE_TRAINER_TOT_CODE
                            + request.getCode());
        }

        masterDataMapper.updateHrTrainerTotEntity(entity, request);

        MasterDataHrTrainerTot updated =
                hrTrainerTotRepository.save(entity);

        return masterDataMapper.toHrTrainerTotResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public HrTrainerTotResponse getHrTrainerTotById(Long id) {

        MasterDataHrTrainerTot entity =
                hrTrainerTotRepository
                        .findByIdAndIsDeleted(id, false)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MasterDataConstants.RECORD_NOT_FOUND
                                                + id));

        return masterDataMapper.toHrTrainerTotResponseDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrTrainerTotResponse> getAllHrTrainerTots() {

        return hrTrainerTotRepository
                .findAllByIsDeletedOrderByFullNameAsc(false)
                .stream()
                .map(masterDataMapper::toHrTrainerTotResponseDto)
                .collect(Collectors.toList());
    }


}
