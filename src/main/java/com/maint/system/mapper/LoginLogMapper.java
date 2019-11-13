package com.maint.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.maint.system.model.LoginLog;

import java.util.List;

@Mapper
public interface LoginLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoginLog loginLog);

    LoginLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(LoginLog loginLog);

    List<LoginLog> selectAll();

    List<Integer> recentlyWeekLoginCount(@Param("username") String username);

    int count();
}