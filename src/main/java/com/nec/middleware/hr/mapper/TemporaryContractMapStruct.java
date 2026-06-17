package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.TemporaryContractRequestDto;
import com.nec.middleware.hr.dto.response.TemporaryContractResponseDto;
import com.nec.middleware.hr.entity.TemporaryContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TemporaryContractMapStruct {

    TemporaryContract temporaryContractEntity(TemporaryContractRequestDto dto);
    @Mapping(target = "contractType.id", source = "contractType.id")
    @Mapping(target = "contractType.value", source = "contractType.value")
    TemporaryContractResponseDto temporaryContractResponseDto(TemporaryContract temporaryContractEntity);


    void updateEntityFromDto(TemporaryContractRequestDto temporaryContractRequestDto,
            @MappingTarget TemporaryContract temporaryContractEntity);
}
