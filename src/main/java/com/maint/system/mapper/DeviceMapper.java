package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.maint.system.model.Device;

public interface DeviceMapper {
    int deleteByPrimaryKey(String deviceId);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(String deviceId);
    
    List<Device> selectByCompanyId(@Param("companyId") String companyId);
    
    int delByCompanyId(@Param("companyId") String companyId);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}