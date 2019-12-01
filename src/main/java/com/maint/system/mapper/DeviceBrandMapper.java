package com.maint.system.mapper;

import com.maint.system.model.DeviceBrand;

public interface DeviceBrandMapper {
    int deleteByPrimaryKey(String brandId);

    int insert(DeviceBrand record);

    int insertSelective(DeviceBrand record);

    DeviceBrand selectByPrimaryKey(String brandId);

    int updateByPrimaryKeySelective(DeviceBrand record);

    int updateByPrimaryKey(DeviceBrand record);
}