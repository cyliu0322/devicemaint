package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.Company;

public interface CompanyMapper {
	@Select("SELECT '1' FROM tbl_company WHERE company_name=#{companyName} LIMIT 1")
	String isExistCompany(@Param("companyName")String companyName);
	
    int deleteByPrimaryKey(String companyId);

    int insert(Company record);

    int insertSelective(Company record);

    Company selectByPrimaryKey(String companyId);
    
    /**
     * 查询所有大客户
     * @return
     */
    List<Company> selectAllWithQuery(Company company);
    
    /**
     * 统计已经有几个此大客户名称, 用来检测是否重复.
     */
    int countByCompanyName(@Param("companyName") String companyName);
    
    /**
     * 统计已经有几个此大客户名称, 用来检测是否重复 (不包含某品大客户ID).
     */
    int countByCompanyNameNotIncludeCompanyId(@Param("companyName") String companyName, @Param("companyId") String companyId);

    int updateByPrimaryKeySelective(Company record);

    int updateByPrimaryKey(Company record);
}