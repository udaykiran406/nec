package com.nec.middleware.rbacAuth.rbac.mapper;

import com.nec.middleware.Lookups.entity.Departments;
import com.nec.middleware.Lookups.entity.Genders;
import com.nec.middleware.masterdata.entity.MasterDataCity;
import com.nec.middleware.masterdata.entity.MasterDataDistrict;
import com.nec.middleware.masterdata.entity.MasterDataRegion;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.CityResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.DepartmentResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.DistrictResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GenderResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.GroupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.LookupResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.ModuleResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.PermissionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.RegionResponse;
import com.nec.middleware.rbacAuth.rbac.dto.response.nested.RoleResponse;
import com.nec.middleware.rbacAuth.rbac.entity.RbacModule;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermission;
import com.nec.middleware.rbacAuth.rbac.entity.RbacPermissionGroup;
import com.nec.middleware.rbacAuth.rbac.entity.RbacRole;
import org.springframework.stereotype.Component;

@Component
public class AssociationMapper {

    public RoleResponse toRole(RbacRole entity) {
        if (entity == null) {
            return null;
        }
        return RoleResponse.builder()
                .roleId(entity.getRoleId())
                .roleCode(entity.getRoleCode())
                .roleName(entity.getRoleName())
                .build();
    }

    public ModuleResponse toModule(RbacModule entity) {
        if (entity == null) {
            return null;
        }
        return ModuleResponse.builder()
                .moduleCode(entity.getModuleCode())
                .moduleName(entity.getModuleName())
                .build();
    }

    public GroupResponse toGroup(RbacPermissionGroup entity) {
        if (entity == null) {
            return null;
        }
        return GroupResponse.builder()
                .groupCode(entity.getGroupCode())
                .groupName(entity.getGroupName())
                .build();
    }

    public PermissionResponse toPermission(RbacPermission entity) {
        if (entity == null) {
            return null;
        }
        return PermissionResponse.builder()
                .permissionCode(entity.getPermissionCode())
                .permissionName(entity.getPermissionName())
                .build();
    }

    public GenderResponse toGender(Genders entity) {
        if (entity == null) {
            return null;
        }
        return GenderResponse.builder()
                .id(entity.getId())
                .genderName(entity.getValue())
                .build();
    }

    public DepartmentResponse toDepartment(Departments entity) {
        if (entity == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(entity.getId())
                .departmentName(entity.getValue())
                .build();
    }

    public LookupResponse toLookup(Genders entity) {
        if (entity == null) {
            return null;
        }
        return LookupResponse.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .build();
    }

    public LookupResponse toLookup(Departments entity) {
        if (entity == null) {
            return null;
        }
        return LookupResponse.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .build();
    }

    public RegionResponse toRegion(MasterDataRegion entity) {
        if (entity == null) {
            return null;
        }
        return RegionResponse.builder()
                .id(entity.getId())
                .regionName(entity.getRegionName())
                .build();
    }

    public DistrictResponse toDistrict(MasterDataDistrict entity) {
        if (entity == null) {
            return null;
        }
        return DistrictResponse.builder()
                .id(entity.getId())
                .districtName(entity.getDistrictName())
                .build();
    }

    public CityResponse toCity(MasterDataCity entity) {
        if (entity == null) {
            return null;
        }
        return CityResponse.builder()
                .id(entity.getId())
                .cityName(entity.getCityName())
                .build();
    }
}
