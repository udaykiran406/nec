package com.nec.middleware.masterdata.service;

import com.nec.middleware.masterdata.dto.request.*;
import com.nec.middleware.masterdata.dto.response.CityResponse;
import com.nec.middleware.masterdata.dto.response.DistrictResponse;
import com.nec.middleware.masterdata.dto.response.MasterDataResponse;
import com.nec.middleware.masterdata.dto.response.PollingStationResponse;
import com.nec.middleware.masterdata.dto.response.VoterRegistrationCenterResponse;
import com.nec.middleware.masterdata.dto.response.UniversityResponse;
import com.nec.middleware.masterdata.dto.response.WarehouseResponse;
import com.nec.middleware.masterdata.dto.response.WarehouseSubStoreResponse;
import com.nec.middleware.masterdata.dto.response.PoliticalPartyResponse;
import com.nec.middleware.masterdata.dto.response.BankAccountResponse;
import com.nec.middleware.masterdata.dto.response.DeploymentRoleResponse;
import com.nec.middleware.masterdata.dto.response.HrTrainerTotResponse;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MasterDataService {

    MasterDataResponse saveRegion(MasterDataRequest request);

    List<MasterDataResponse> getAllRegions();


    MasterDataResponse getRegionById(Long id);

    DistrictResponse saveDistrict(DistrictRequest request);

    Page<DistrictResponse> getAllDistricts(DistrictListRequestDto filterDto);

    DistrictResponse getDistrictById(Long id);

    CityResponse saveCity(CityRequest request);

    Page<CityResponse> getAllCities(CityListRequestDto filterDto);


    CityResponse getCityById(Long id);

    PollingStationResponse savePollingStation(PollingStationRequest request);

    Page<PollingStationResponse> getAllPollingStations(PollingStationListRequestDto filterDto);


    PollingStationResponse getPollingStationById(Long id);

    VoterRegistrationCenterResponse saveVoterRegistrationCenter(
            VoterRegistrationCenterRequest request);

    Page<VoterRegistrationCenterResponse> getAllVoterRegistrationCenters(
            VoterRegistrationCenterListRequestDto filterDto);

    VoterRegistrationCenterResponse getVoterRegistrationCenterById(Long id);

    UniversityResponse saveUniversity(UniversityRequest request);



    Page<UniversityResponse> getAllUniversities(
            UniversityListRequestDto filterDto);


    UniversityResponse getUniversityById(Long id);

    WarehouseResponse saveWarehouse(WarehouseRequest request);

    Page<WarehouseResponse> getAllWarehouses(WarehouseListRequestDto filterDto);


    WarehouseResponse getWarehouseById(Long id);

    WarehouseSubStoreResponse saveWarehouseSubStore(WarehouseSubStoreRequest request);

    Page<WarehouseSubStoreResponse> getAllWarehouseSubStores(WarehouseSubStoreListRequestDto filterDto);


    WarehouseSubStoreResponse getWarehouseSubStoreById(Long id);

    PoliticalPartyResponse savePoliticalParty(PoliticalPartyRequest request);

    List<PoliticalPartyResponse> getAllPoliticalParties();


    PoliticalPartyResponse getPoliticalPartyById(Long id);

    BankAccountResponse saveBankAccount(BankAccountRequest request);

    Page<BankAccountResponse> getAllBankAccounts(BankAccountListRequestDto filterDto);


    BankAccountResponse getBankAccountById(Long id);

    DeploymentRoleResponse saveDeploymentRole(DeploymentRoleRequest request);

    List<DeploymentRoleResponse> getAllDeploymentRoles();


    DeploymentRoleResponse getDeploymentRoleById(Long id);


    HrTrainerTotResponse saveHrTrainerTot(HrTrainerTotRequest request);

    List<HrTrainerTotResponse> getAllHrTrainerTots();


    HrTrainerTotResponse getHrTrainerTotById(Long id);


}