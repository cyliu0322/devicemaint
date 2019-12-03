package com.maint.system.mapper;

import com.maint.system.model.MaintainMaterial;

public interface MaintainMaterialMapper {
    int insert(MaintainMaterial record);

    int insertSelective(MaintainMaterial record);
}