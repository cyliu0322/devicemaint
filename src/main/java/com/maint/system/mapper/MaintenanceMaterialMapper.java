package com.maint.system.mapper;

import com.maint.system.model.MaintenanceMaterial;

public interface MaintenanceMaterialMapper {
    int insert(MaintenanceMaterial record);

    int insertSelective(MaintenanceMaterial record);
}