package com.maint.system.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.pagehelper.PageHelper;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.model.Company;
import com.maint.system.model.Device;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.User;
import com.maint.system.model.vo.MaintVO;

import javax.annotation.Resource;

import java.util.List;
import java.util.Random;

@Service
public class MaintService {

	@Resource
	private MaintainOrderMapper maintMapper;
	
	@Resource
	private MaintainTraceMapper traceMapper;
	
	@Resource
	private VipService vipService;
	
	@Resource
	private DeviceService deviceService;
	
	public List<MaintainOrder> selectAllWithQuery(int page, int rows, MaintainOrder maintQuery) {
		PageHelper.startPage(page, rows);
		List<MaintainOrder> maints = maintMapper.selectAllWithQuery(maintQuery);
		for (MaintainOrder maintainOrder : maints) {
			maintainOrder.setStateDesc(MaintainOrderStatusEnum.getvalueOf(maintainOrder.getState()).getTxt());
		}
		return maints;
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
			
			companyId = vipService.insert(company);
		}
		
		if (deviceId.equals("")) {	//新设备
			Device device = new Device();
			device.setDeviceName(maintVO.getDeviceName());
			device.setCode(maintVO.getDeviceCode());
			device.setFirstTime(maintVO.getFirstTime());
			device.setAddress(maintVO.getCompanyAddress());
			device.setCompanyId(companyId);
			device.setBrandId(maintVO.getBrandId());
			
			deviceService.add(device);
		}
		
		String maintOrderId = generateCode(0, "WX");
		
		MaintainTrace trace = new MaintainTrace();
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		
		// 新增维修单追踪记录
		trace.setMaintainTraceId(generateCode(1, "WT"));
		trace.setMaintainOrderId(maintOrderId);
		trace.setOrderStatus(MaintainOrderStatusEnum.WXSQ.getValue());
		trace.setUserId(currentUser.getUserId());
		
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
//		maintainOrder.setUserId(userId);
		
		int result = maintMapper.insert(maintainOrder);
		
		// 成功后发送信息至客户及管理员
		
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
		
		traceMapper.insert(trace);
		
		return maintMapper.updateByPrimaryKeySelective(maint) == 1 ? true : false;
		
		//TODO
		//短信发送服务
	}
	
	@Transactional
	public void delete(String maintId) {
		// 删除订单追踪表
		traceMapper.deleteByMaintId(maintId);
		
		maintMapper.deleteByPrimaryKey(maintId);
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
