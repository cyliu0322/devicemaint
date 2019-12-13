package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.maint.system.model.DeviceBrand;

public interface DeviceBrandMapper {
    int deleteByPrimaryKey(String brandId);

    int insert(DeviceBrand record);

    int insertSelective(DeviceBrand record);

    DeviceBrand selectByPrimaryKey(String brandId);
    
    List<DeviceBrand> selectAllWithQuery(DeviceBrand brand);

    /**
     * 统计已经有几个此品牌名称, 用来检测是否重复.
     */
    int countByBrandName(@Param("brandName") String brandName);
    
    /**
     * 统计已经有几个此品牌名称, 用来检测是否重复 (不包含某品牌ID).
     */
    int countByBrandNameNotIncludeBrandId(@Param("brandName") String brandName, @Param("brandId") String brandId);
    
    int updateByPrimaryKeySelective(DeviceBrand record);

    int updateByPrimaryKey(DeviceBrand record);
}