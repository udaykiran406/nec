package com.nec.middleware.hr.mapper;

import com.nec.middleware.hr.dto.request.PortalUserRequestDto;
import com.nec.middleware.hr.dto.response.PortalUserResponseDto;
import com.nec.middleware.hr.entity.PortalUser;

import org.springframework.stereotype.Component;

@Component
public class PortalUserMapper {

    /**
     * Map RequestDto → new Entity (for CREATE)
     */
    public PortalUser portalUserEntity(PortalUserRequestDto dto) {
        PortalUser portalUserEntity = PortalUser.builder()
                .userName(dto.getUserName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .photoPath(dto.getPhotoPath())
                .faculty(dto.getFaculty())
                .build();

        portalUserEntity.setIsActive(Boolean.TRUE);
        portalUserEntity.setCreatedBy(dto.getCreatedBy());
        portalUserEntity.setUpdatedBy(dto.getCreatedBy());

        return portalUserEntity;
    }

    /**
     * Merge RequestDto → existing Entity (for UPDATE)
     */
    public void updatePortalUserEntity(PortalUser portalUser, PortalUserRequestDto portalUserRequestDto) {

        if (portalUserRequestDto.getUserName() != null) {
            portalUser.setUserName(portalUserRequestDto.getUserName());
        }

        if (portalUserRequestDto.getPhone() != null) {
            portalUser.setPhone(portalUserRequestDto.getPhone());
        }

        if (portalUserRequestDto.getEmail() != null) {
            portalUser.setEmail(portalUserRequestDto.getEmail());
        }

        if (portalUserRequestDto.getPhotoPath() != null) {
            portalUser.setPhotoPath(portalUserRequestDto.getPhotoPath());
        }

        if (portalUserRequestDto.getFaculty() != null) {
            portalUser.setFaculty(portalUserRequestDto.getFaculty());
        }
    }

    /**
     * Map Entity → ResponseDto
     */
    public PortalUserResponseDto portalUserResponseDto(PortalUser entity) {
        return PortalUserResponseDto.builder()
                // Identity
                .id(entity.getId())
                .portalUserId(entity.getPortalUserId())
                .userName(entity.getUserName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .photoPath(entity.getPhotoPath())
                .faculty(entity.getFaculty())

                // Lookup FKs — name resolved via lazy-loaded association
//                .genderId(entity.getGenderId())
                .genderName(entity.getGender() != null
                        ? entity.getGender().getValue() : null)

                .roleName(entity.getRole() != null
                        ? entity.getRole().getValue() : null)

                .portalUserTypeName(entity.getPortalUserType() != null
                        ? entity.getPortalUserType().getValue() : null)

                // Master data FKs — name resolved via lazy-loaded association
                .universityName(entity.getUniversity() != null
                        ? entity.getUniversity().getUniversityName() : null)

                .regionName(entity.getRegion() != null
                        ? entity.getRegion().getRegionName() : null)

                .districtName(entity.getDistrict() != null
                        ? entity.getDistrict().getDistrictName() : null)

                .cityName(entity.getCity() != null
                        ? entity.getCity().getCityName() : null)

                // Audit
                .isActive(entity.getIsActive())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}
