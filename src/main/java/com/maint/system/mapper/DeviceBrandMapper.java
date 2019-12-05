package com.maint.system.mapper;

import java.util.List;

import com.maint.system.model.DeviceBrand;

public interface DeviceBrandMapper {
    int deleteByPrimaryKey(String brandId);

    int insert(DeviceBrand record);

    int insertSelective(DeviceBrand record);

    DeviceBrand selectByPrimaryKey(String brandId);
    
    List<DeviceBrand> selectAllWithQuery(DeviceBrand brand);

    int updateByPrimaryKeySelective(DeviceBrand record);

    int updateByPrimaryKey(DeviceBrand record);
}