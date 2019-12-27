package com.maint.system.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintenanceOrderStatusEnum;
import com.maint.system.mapper.DeptMapper;
import com.maint.system.mapper.MaintenanceOrderMapper;
import com.maint.system.mapper.MaintenanceTraceMapper;
import com.maint.system.model.DateAndNum;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.MaintenanceTrace;
import com.maint.system.model.User;

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
		trace.setFaultCause("新增保养单。");
		
		traceMapper.insert(trace);
		
		maintenance.setState(MaintenanceOrderStatusEnum.BYDSC.getValue());
		maintenance.setMaintenanceOrderId(maintenanceId);
		maintenance.setUserId(currentUser.getUserId());
		
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
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		
		MaintenanceTrace trace = new MaintenanceTrace();
		
		trace.setMaintenanceTraceId(generateCode(1, "TE"));
		trace.setMaintenanceOrderId(maintenance.getMaintenanceOrderId());
		trace.setOrderStatus(maintenance.getState());
		trace.setUserId(currentUser.getUserId());
		trace.setFaultCause("保养派单，维修点：" + deptMapper.selectByPrimaryKey(maintenance.getDeptId()).getDeptName());
		
		traceMapper.insert(trace);
		
		return maintenanceMapper.updateByPrimaryKeySelective(maintenance) == 1 ? true : false;
	}
	
	@Transactional
	public void delete(String maintenanceId) {
		// 删除订单追踪表
		traceMapper.deleteByMaintenanceId(maintenanceId);
		
		maintenanceMapper.deleteByPrimaryKey(maintenanceId);
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
