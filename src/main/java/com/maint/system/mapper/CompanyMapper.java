package com.maint.system.mapper;

import java.util.List;

import com.maint.system.model.Company;

public interface CompanyMapper {
    int deleteByPrimaryKey(String companyId);

    int insert(Company record);

    int insertSelective(Company record);

    Company selectByPrimaryKey(String companyId);
    
    List<Company> selectAllCompany();

    int updateByPrimaryKeySelective(Company record);

    int updateByPrimaryKey(Company record);
}