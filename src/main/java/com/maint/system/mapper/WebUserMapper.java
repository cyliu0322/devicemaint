package com.maint.system.mapper;

import com.maint.system.model.WebUser;

public interface WebUserMapper {
    int deleteByPrimaryKey(Integer webUserId);

    int insert(WebUser record);

    int insertSelective(WebUser record);

    WebUser selectByPrimaryKey(Integer webUserId);

    int updateByPrimaryKeySelective(WebUser record);

    int updateByPrimaryKey(WebUser record);
}