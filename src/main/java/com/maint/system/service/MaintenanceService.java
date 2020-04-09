package com.maint.system.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintenanceOrderStatusEnum;
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.enums.RoleEnum;
import com.maint.system.mapper.DeptMapper;
import com.maint.system.mapper.MaintenanceOrderMapper;
import com.maint.system.mapper.MaintenanceTraceMapper;
import com.maint.system.model.DateAndNum;
import com.maint.system.model.Dept;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.MaintenanceTrace;
import com.maint.system.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Service
public class MaintenanceService {

	@Resource
	private MaintenanceOrderMapper maintenanceMapper;
	
	@Resource
	private MaintenanceTraceMapper traceMapper;
	
	@Resource
	private DeptMapper deptMapper;
	
	public List<DateAndNum> selectCountForApply() {
		return traceMapper.selectCountForApply();
	}
	
	public List<DateAndNum> selectCountForComplete() {
		return traceMapper.selectCountForComplete();
	}
	
	public List<MaintenanceOrder> selectAllWithQuery(int page, int rows, MaintenanceOrder maintenanceQuery) {
		PageHelper.startPage(page, rows);
		List<MaintenanceOrder> maintenances = maintenanceMapper.selectAllWithQuery(maintenanceQuery);
		for (MaintenanceOrder maintenance : maintenances) {
			maintenance.setStateDesc(MaintenanceOrderStatusEnum.getvalueOf(maintenance.getState()).getTxt());
		}
		return maintenances;
	}
	
	public List<MaintenanceOrder> selectWithQueryAndRole(int page, int rows, MaintenanceOrder maintenanceQuery) {
		// 1、超级管理员、系统管理员、客服人员可查询范围：全部
		// 2、管理员（维护人员）、维修点负责人可查询范围：当前负责人=user_id + 经手操作过的订单（去重）
		Subject subject = SecurityUtils.getSubject();
		User currentUser = (User) subject.getPrincipal();	// 当前用户
		PageHelper.startPage(page, rows);
		
		List<MaintenanceOrder> maintenances = new ArrayList<MaintenanceOrder>();	// 订单结果集
		List<String> ids = new ArrayList<String>();			// 订单id结果集
		List<String> operateIds = new ArrayList<String>();	// 经手操作订单id集合
		
		if (subject.hasRole(RoleEnum.SUPER.toString()) || subject.hasRole(RoleEnum.SYSTEM.toString())
				|| subject.hasRole(RoleEnum.SUPPORT.toString())) {
			maintenances = maintenanceMapper.selectAllWithQuery(maintenanceQuery);
		} else if (subject.hasRole(RoleEnum.ADMIN.toString()) || subject.hasRole(RoleEnum.CHARGER.toString())) {
			ids = maintenanceMapper.selectWithUserId(currentUser.getUserId());	// 当前负责人订单id集合
			operateIds = traceMapper.selectOrderIdsByUserId(currentUser.getUserId());
			boolean duplicate = false;		// 订单id是否重复
			for (String orderId : operateIds) {
				for (String chargeId : ids) {
					// 将两个结果集去重
					if (!duplicate && chargeId.equals(orderId)) {
						duplicate = true;
					}
				}
				if (!duplicate) {
					ids.add(orderId);
					duplicate = false;
				}
			}
			maintenances = maintenanceMapper.selectWithQueryAndIds(maintenanceQuery, ids);
		}
		
		for (MaintenanceOrder maintenance : maintenances) {
			maintenance.setStateDesc(MaintenanceOrderStatusEnum.getvalueOf(maintenance.getState()).getTxt());
		}
		return maintenances;
	}
	
	public MaintenanceOrder selectByMaintenanceId(String maintenanceId) {
		return maintenanceMapper.selectByPrimaryKey(maintenanceId);
	}
	
	@Transactional
	public boolean add(MaintenanceOrder maintenance) {
		String maintenanceId = generateCode(0, "BY");
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		
		MaintenanceTrace trace = new MaintenanceTrace();
		trace.setMaintenanceTraceId(generateCode(1, "BT"));
		trace.setMaintenanceOrderId(maintenanceId);
		trace.setUserId(currentUser.getUserId());
		trace.setOrderStatus(MaintenanceOrderStatusEnum.BYDSC.getValue());
		trace.setFaultCause("新增保养单");
		traceMapper.insert(trace);
		
		maintenance.setState(MaintenanceOrderStatusEnum.BYDSC.getValue());
		maintenance.setMaintenanceOrderId(maintenanceId);
		maintenance.setUserId(currentUser.getUserId());	// 操作用户为当前负责人
		
		return maintenanceMapper.insert(maintenance) == 1 ? true : false;
	}
	
	public List<MaintenanceTrace> getTracesByMaintenanceId(String maintenanceId) {
		List<MaintenanceTrace> traces = traceMapper.selectByMaintenanceId(maintenanceId);
		for (MaintenanceTrace trace : traces) {
			trace.setStatusDesc(MaintenanceOrderStatusEnum.getvalueOf(trace.getOrderStatus()).getTxt());
		}
		return traces;
	}
	
	@Transactional
	public boolean appoint(MaintenanceOrder maintenance) {
		// 当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		
		MaintenanceTrace trace = new MaintenanceTrace();
		trace.setMaintenanceTraceId(generateCode(1, "BT"));
		trace.setMaintenanceOrderId(maintenance.getMaintenanceOrderId());
		trace.setOrderStatus(maintenance.getState());
		trace.setUserId(currentUser.getUserId());
		trace.setFaultCause("派单至维修点，维修点：" + deptMapper.selectByPrimaryKey(maintenance.getDeptId()).getDeptName());
		traceMapper.insert(trace);
		
		// 根据维修点更新当前负责人
		Dept dept = deptMapper.selectByPrimaryKey(maintenance.getDeptId());
		maintenance.setUserId(dept.getUserId());
		
		return maintenanceMapper.updateByPrimaryKeySelective(maintenance) == 1 ? true : false;
	}
	
	@Transactional
	public void delete(String maintenanceId) {
		// 删除订单追踪表
		traceMapper.deleteByMaintenanceId(maintenanceId);
		// 删除订单记录
		maintenanceMapper.deleteByPrimaryKey(maintenanceId);
	}
	
	/**
	 * 保养订单实施
	 * @param orderId
	 */
	@Transactional
	public void implement(String orderId) {
		// 订单记录追踪
		MaintenanceTrace trace = new MaintenanceTrace();
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		trace.setMaintenanceOrderId(generateCode(1, "BT"));
		trace.setMaintenanceOrderId(orderId);
		trace.setOrderStatus(MaintenanceOrderStatusEnum.BYZ.getValue());
		trace.setUserId(currentUser.getUserId());
		trace.setFaultCause("保养订单实施");
		traceMapper.insert(trace);
		
		// 实施时当前负责人不变化
		MaintenanceOrder mo = maintenanceMapper.selectByPrimaryKey(orderId);
		mo.setState(MaintenanceOrderStatusEnum.BYZ.getValue());
		maintenanceMapper.updateByPrimaryKey(mo);
	}
	
	private String generateCode(int flag, String prefix) {
		String code = StringUtil.generateCode(prefix);
		//校验是否重复
		if (flag == 0) {	//保养单
			MaintenanceOrder maintenance = maintenanceMapper.selectByPrimaryKey(code);
			if (maintenance != null) {
				code = generateCode(flag, prefix);
			}
		} else {	//跟踪
			MaintenanceTrace trace = traceMapper.selectByPrimaryKey(code);
			if (trace != null) {
				code = generateCode(flag, prefix);
			}
		}
		
		return code;
	}
}
