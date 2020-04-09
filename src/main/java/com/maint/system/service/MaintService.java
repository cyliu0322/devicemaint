package com.maint.system.service;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.enums.RoleEnum;
import com.maint.system.mapper.DeptMapper;
import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.mapper.UserMapper;
import com.maint.system.mapper.UserRoleMapper;
import com.maint.system.model.Company;
import com.maint.system.model.DateAndNum;
import com.maint.system.model.Dept;
import com.maint.system.model.Device;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.User;
import com.maint.system.model.vo.MaintVO;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MaintService {

	@Resource
	private MaintainOrderMapper maintMapper;
	
	@Resource
	private MaintainTraceMapper traceMapper;
	
	@Resource
	private UserMapper userMapper;
	
	@Resource
	private DeptMapper deptMapper;
	
	@Resource
	private UserRoleMapper userRoleMapper;
	
	@Resource
	private VipService vipService;
	
	@Resource
	private DeviceService deviceService;
	
	public List<DateAndNum> selectCountForApply() {
		return traceMapper.selectCountForApply();
	}
	
	public List<DateAndNum> selectCountForComplete() {
		return traceMapper.selectCountForComplete();
	}
	
	public List<MaintainOrder> selectAllWithQuery(int page, int rows, MaintainOrder maintQuery) {
		PageHelper.startPage(page, rows);
		List<MaintainOrder> maints = maintMapper.selectAllWithQuery(maintQuery);
		for (MaintainOrder maintainOrder : maints) {
			maintainOrder.setStateDesc(MaintainOrderStatusEnum.getvalueOf(maintainOrder.getState()).getTxt());
		}
		return maints;
	}
	
	public List<MaintainOrder> selectWithQueryAndRole(int page, int rows, MaintainOrder maintQuery) {
		// 1、超级管理员，系统管理员、客服人员可查询范围：全部
		// 2、管理员可查询范围：状态是维修申请的订单 + 当前负责人=user_id + 经手操作过的订单（去重）
		// 3、首检人员、维修点负责人可查询范围：当前负责人=user_id + 经手操作过的订单（去重）
		Subject subject = SecurityUtils.getSubject();
		User currentUser = (User) subject.getPrincipal();	// 当前用户
		PageHelper.startPage(page, rows);
		
		List<MaintainOrder> maints = new ArrayList<MaintainOrder>();	// 订单结果集
		List<String> ids = new ArrayList<String>();			// 订单id结果集
		List<String> chargeIds = new ArrayList<String>();	// 当前负责人订单id集合
		List<String> operateIds = new ArrayList<String>();	// 经手操作订单id集合
		
		if (subject.hasRole(RoleEnum.SUPER.toString()) || subject.hasRole(RoleEnum.SYSTEM.toString())
				|| subject.hasRole(RoleEnum.SUPPORT.toString())) {
			maints = maintMapper.selectAllWithQuery(maintQuery);
		} else if (subject.hasRole(RoleEnum.ADMIN.toString())) {
			ids = maintMapper.selectWithState(MaintainOrderStatusEnum.WXSQ.getValue());	// 维修申请状态订单id集合
			chargeIds = maintMapper.selectWithUserId(currentUser.getUserId());
			operateIds = traceMapper.selectOrderIdsByUserId(currentUser.getUserId());
			boolean duplicate = false;		// 订单id是否重复
			for (String operateId : operateIds) {
				for (String chargeId : chargeIds) {
					// 并入结果集
					ids.add(chargeId);
					// 将两个结果集去重
					if (!duplicate && chargeId.equals(operateId)) {
						duplicate = true;
					}
				}
				if (!duplicate) {
					ids.add(operateId);
					duplicate = false;
				}
			}
			maints = maintMapper.selectWithQueryAndIds(maintQuery, ids);
		} else if (subject.hasRole(RoleEnum.VANGUARD.toString()) || subject.hasRole(RoleEnum.CHARGER.toString())) {
			ids = maintMapper.selectWithUserId(currentUser.getUserId());	// 当前负责人订单id集合
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
			maints = maintMapper.selectWithQueryAndIds(maintQuery, ids);
		}
		
		for (MaintainOrder maintainOrder : maints) {
			maintainOrder.setStateDesc(MaintainOrderStatusEnum.getvalueOf(maintainOrder.getState()).getTxt());
		}
		
		return maints;
	}
	
	public int countByState(MaintainOrderStatusEnum status) {
		return maintMapper.countByState(status.getValue());
	}
	
	public MaintainOrder selectByMaintId(String maintId) {
		return maintMapper.selectByPrimaryKey(maintId);
	}
	
	public List<MaintainTrace> selectTracesByMaintId(String maintId) {
		List<MaintainTrace> traces = traceMapper.selectByMaintId(maintId);
		for (MaintainTrace maintainTrace : traces) {
			maintainTrace.setStatusDesc(MaintainOrderStatusEnum.getvalueOf(maintainTrace.getOrderStatus()).getTxt());
		}
		return traces;
	}
	
	@Transactional
	public int add(MaintVO maintVO) {
		String companyId = maintVO.getCompanyId();
		String deviceId = maintVO.getDeviceId();
		
		if (companyId.equals("")) {	//新客户
			Company company = new Company();
			company.setCompanyName(maintVO.getCompanyName());
			company.setContact(maintVO.getCompanyContact());
			company.setPhone(maintVO.getCompanyPhone());
			company.setCompanyAddress(maintVO.getCompanyAddress());
			company.setDeptId(maintVO.getDeptId());
			company.setEmail(maintVO.getEmail());
			companyId = vipService.insert(company);
		}
		
		if (deviceId.equals("")) {	//新设备
			Device device = new Device();
			device.setDeviceName(maintVO.getDeviceName());
			device.setCode(maintVO.getDeviceCode());
			device.setSerialNumber(maintVO.getDeviceModel());
			device.setFirstTime(maintVO.getFirstTime() == null ? new Date() : maintVO.getFirstTime());
			device.setYears(maintVO.getYears());
			device.setLastMaintenanceTime(maintVO.getMaintTime() == null ? new Date() : maintVO.getMaintTime());
			device.setAddress(maintVO.getMaintAddress());
			device.setCompanyId(companyId);
			device.setBrandId(maintVO.getBrandId());
			deviceService.add(device);
		}
		
		String maintOrderId = generateCode(0, "WX");	// 生成维修单id
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		
		// 新增维修单追踪记录
		MaintainTrace trace = new MaintainTrace();
		trace.setMaintainTraceId(generateCode(1, "WT"));
		trace.setMaintainOrderId(maintOrderId);
		trace.setOrderStatus(MaintainOrderStatusEnum.WXSQ.getValue());
		trace.setUserId(currentUser.getUserId());
		trace.setFaultCause("新增维修单");
		traceMapper.insert(trace);
		
		// 新增维修单
		MaintainOrder maintainOrder = new MaintainOrder();
		maintainOrder.setMaintainOrderId(maintOrderId);
		maintainOrder.setCompanyName(maintVO.getCompanyName());
		maintainOrder.setCompanyId(companyId);
		maintainOrder.setContact(maintVO.getMaintContact());
		maintainOrder.setPhone(maintVO.getMaintPhone());
		maintainOrder.setDeviceName(maintVO.getDeviceName());
		maintainOrder.setDeviceCode(maintVO.getDeviceCode());
		maintainOrder.setDeviceBrand(maintVO.getBrandId());
		maintainOrder.setAddress(maintVO.getMaintAddress());
		maintainOrder.setState(MaintainOrderStatusEnum.WXSQ.getValue());
		maintainOrder.setDeptId(maintVO.getDeptId());
		maintainOrder.setFaultDescription(maintVO.getMaintDesc());
		maintainOrder.setUserId(currentUser.getUserId());	// 当前负责人
		int result = maintMapper.insert(maintainOrder);
		
		// 成功后发送信息至客户及管理员
		// TODO
		
		return result;
	}
	
	@Transactional
	public boolean appoint(MaintainOrder maint) {
		MaintainTrace trace = new MaintainTrace();
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		trace.setMaintainTraceId(generateCode(1, "WT"));
		trace.setMaintainOrderId(maint.getMaintainOrderId());
		trace.setOrderStatus(maint.getState());
		trace.setUserId(currentUser.getUserId());
		if (maint.getState().equals(MaintainOrderStatusEnum.DSJ.getValue())) {
			trace.setFaultCause("派单至首检人员，首检人：" + userMapper.selectByPrimaryKey(maint.getUserId()).getNickname());
		} else {
			trace.setFaultCause("派单至维修点，维修点：" + deptMapper.selectByPrimaryKey(maint.getDeptId()).getDeptName());
			// 根据维修点id查询维修点负责人
			Dept dept = deptMapper.selectByPrimaryKey(maint.getDeptId());
			maint.setUserId(dept.getUserId());
		}
		traceMapper.insert(trace);
		
		return maintMapper.updateByPrimaryKeySelective(maint) == 1 ? true : false;
		
		//TODO
		//短信发送服务
	}
	
	/**
	 * 维修订单删除
	 * @param maintId
	 */
	@Transactional
	public void delete(String maintId) {
		// 删除订单追踪记录
		traceMapper.deleteByMaintId(maintId);
		// 删除订单记录
		maintMapper.deleteByPrimaryKey(maintId);
	}
	
	/**
	 * 维修订单实施
	 * @param orderId
	 */
	@Transactional
	public void implement(String orderId) {
		// 订单追踪记录
		MaintainTrace trace = new MaintainTrace();
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		trace.setMaintainTraceId(generateCode(1, "WT"));
		trace.setMaintainOrderId(orderId);
		trace.setOrderStatus(MaintainOrderStatusEnum.WXZ.getValue());
		trace.setUserId(currentUser.getUserId());
		trace.setFaultCause("维修订单实施");
		traceMapper.insert(trace);
		
		// 实施时当前负责人不变化
		MaintainOrder mo = maintMapper.selectByPrimaryKey(orderId);
		mo.setState(MaintainOrderStatusEnum.WXZ.getValue());
		maintMapper.updateByPrimaryKey(mo);
	}
	
	/**
	 * 生产Id
	 * @param type	0：维修单；1：追踪记录
	 * @param prefix	id前缀
	 * @return
	 */
	private String generateCode(int type, String prefix) {
		String code = StringUtil.generateCode(prefix);
		//校验是否重复
		switch (type) {
		case 0:
			MaintainOrder order = maintMapper.selectByPrimaryKey(code);
			if (order != null) {
				code = generateCode(0, prefix);
			}
			break;
		case 1:
			MaintainTrace trace = traceMapper.selectByPrimaryKey(code);
			if (trace != null) {
				code = generateCode(1, prefix);
			}
			break;
		default:
			break;
		}
		
		return code;
	}
}
