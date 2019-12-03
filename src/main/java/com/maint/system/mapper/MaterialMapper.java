package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.maint.system.model.Material;

public interface MaterialMapper {
	@Select(value = "SELECT * FROM tbl_material")
	List<Material> selectMaterials();
	
    int deleteByPrimaryKey(String materialId);

    int insert(Material record);

    int insertSelective(Material record);

    Material selectByPrimaryKey(String materialId);

    int updateByPrimaryKeySelective(Material record);

    int updateByPrimaryKey(Material record);
}