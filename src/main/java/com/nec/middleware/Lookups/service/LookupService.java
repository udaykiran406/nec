package com.nec.middleware.Lookups.service;

import com.nec.middleware.Lookups.dto.LookupRequestDto;
import com.nec.middleware.Lookups.dto.LookupTableResponseDto;
import com.nec.middleware.Lookups.dto.LookupValueResponseDto;

import java.util.List;

public interface LookupService {

    List<LookupTableResponseDto> getAllActiveLookupTables();

    List<LookupValueResponseDto> getLookupValuesByTableName(String tableName);

    LookupValueResponseDto addOrEditLookupValue(LookupRequestDto requestDto);
}