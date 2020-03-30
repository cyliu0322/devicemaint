package com.maint.system.service;

import com.github.pagehelper.PageHelper;
import com.maint.common.shiro.ShiroActionProperties;
import com.maint.common.shiro.realm.ManageRealm;
import com.maint.system.mapper.RoleMapper;
import com.maint.system.mapper.RoleMenuMapper;
import com.maint.system.mapper.RoleOperatorMapper;
import com.maint.system.mapper.UserRoleMapper;
import com.maint.system.model.Role;
import com.maint.system.model.User;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

	@Resource
	private UserRoleMapper userRoleMapper;
	
	@Resource
	private RoleMapper roleMapper;
	
	@Resource
	private RoleMenuMapper roleMenuMapper;
	
	@Resource
	private ManageRealm userNameRealm;
	
	@Resource
	private RoleOperatorMapper roleOperatorMapper;
	
	@Resource
    private ShiroActionProperties shiroActionProperties;
	
	public Role selectOne(Integer roleId) {
		return roleMapper.selectByPrimaryKey(roleId);
	}
	
	public List<Role> selectAllByRole(int page, int limit, Role roleQuery) {
		PageHelper.startPage(page, limit);
		
		//当前用户
    	User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		if (currentUser.getUsername().equals(shiroActionProperties.getSuperAdminUsername())) {
			return roleMapper.selectAllByQuery(roleQuery);
		}
		return roleMapper.selectAllByQueryExceptSysRole(roleQuery);
	}
	
	public List<Role> selectAllByRole() {
		//当前用户
    	User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		if (currentUser.getUsername().equals(shiroActionProperties.getSuperAdminUsername())) {
			return roleMapper.selectAll();
		}
		return roleMapper.selectAllExceptSysRole();
	}
	
	@Transactional
	public void add(Role role) {
		roleMapper.insert(role);
	}
	
	@Transactional
	public void update(Role role) {
		roleMapper.updateByPrimaryKey(role);
	}
	
	/**
	 * 为角色分配菜单
	 * @param roleId
	 *            角色 ID
	 * @param menuIds
	 *            菜单 ID 数组
	 */
	@Transactional
	public void grantMenu(Integer roleId, Integer[] menuIds) {
		roleMenuMapper.deleteByRoleId(roleId);
		if (menuIds != null && menuIds.length != 0) {
			roleMenuMapper.insertRoleMenus(roleId, menuIds);
		}
		clearRoleAuthCache(roleId);
	}
	
	/**
	 * 为角色分配操作权限
	 * @param roleId
	 *            角色 ID
	 * @param operatorIds
	 *            操作权限 ID 数组
	 */
	@Transactional
	public void grantOperator(Integer roleId, Integer[] operatorIds) {
		roleOperatorMapper.deleteByRoleId(roleId);
		if (operatorIds != null && operatorIds.length != 0) {
			for (int i = 0; i < operatorIds.length; i++) {
				operatorIds[i] = operatorIds[i] - 10000;
			}
			roleOperatorMapper.insertRoleOperators(roleId, operatorIds);
		}
		clearRoleAuthCache(roleId);
	}
	
	public int count() {
		return roleMapper.count();
	}
	
	@Transactional
	public void delete(Integer roleId) {
		userRoleMapper.deleteUserRoleByRoleId(roleId);
		roleMapper.deleteByPrimaryKey(roleId);
		roleMenuMapper.deleteByRoleId(roleId);
		roleOperatorMapper.deleteByRoleId(roleId);
	}
	
	public Integer[] getMenusByRoleId(Integer roleId) {
		return roleMenuMapper.getMenusByRoleId(roleId);
	}
	
	public Integer[] getOperatorsByRoleId(Integer roleId) {
		return roleOperatorMapper.getOperatorsByRoleId(roleId);
	}

	private void clearRoleAuthCache(Integer roleId) {
		// 获取该角色下的所有用户.
		List<Integer> userIds = userRoleMapper.selectUserIdByRoleId(roleId);
		
		// 将该角色下所有用户的认证信息缓存清空, 以到达刷新认证信息的目的.
		for (Integer userId : userIds) {
			userNameRealm.clearAuthCacheByUserId(userId);
		}
	}
	
	public List<String> getRoleNameByUserId(Integer userId) {
		List<Integer> roleIds = userRoleMapper.selectRoleIdByUserId(userId);
		List<String> roleNames = new ArrayList<String>();
		for (Integer roleId : roleIds) {
			Role role = roleMapper.selectByPrimaryKey(roleId);
			roleNames.add(role.getRoleName());
		}
		return roleNames;
	}
}