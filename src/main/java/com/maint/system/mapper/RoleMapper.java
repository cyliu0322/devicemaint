package com.maint.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.maint.system.model.Role;

import java.util.List;

@Mapper
public interface RoleMapper {
    int deleteByPrimaryKey(Integer roleId);

    int insert(Role role);

    Role selectByPrimaryKey(Integer roleId);

    int updateByPrimaryKey(Role role);

    List<Role> selectAll();
    
    /**
     * 获取除系统角色外的所有角色
     * @param roleQuery
     * @return
     */
    List<Role> selectAllExceptSysRole();

    List<Role> selectAllByQuery(Role roleQuery);
    
    /**
     * 根据查询条件获取除系统角色外的所有角色
     * @param roleQuery
     * @return
     */
    List<Role> selectAllByQueryExceptSysRole(Role roleQuery);

    int count();
}