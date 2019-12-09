package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.Material;

public interface MaterialMapper {
	@Select(value = "SELECT * FROM tbl_material")
	List<Material> selectMaterials();
	
    int deleteByPrimaryKey(String materialId);

    int insert(Material record);

    int insertSelective(Material record);

    Material selectByPrimaryKey(String materialId);
    
    List<Material> selectAllWithQuery(Material material);
    
    /**
     * 统计已经有几个此材料名称, 用来检测材料名称是否重复.
     */
    int countByMaterialName(@Param("materialName") String materialName);
    
    /**
     * 统计已经有几个此材料名称, 用来检测材料名称是否重复 (不包含某材料 ID).
     */
    int countByMaterialNameNotIncludeMaterialId(@Param("materialName") String materialName, @Param("materialId") String materialId);

    int updateByPrimaryKeySelective(Material record);

    int updateByPrimaryKey(Material record);
}