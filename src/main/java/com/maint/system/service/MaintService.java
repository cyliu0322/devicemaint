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
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.User;

import javax.annotation.Resource;

import java.util.List;
import java.util.Random;

@Service
public class MaintService {

	@Resource
	private MaintainOrderMapper maintMapper;
	
	@Resource
	private MaintainTraceMapper traceMapper;
	
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
	public boolean appoint(MaintainOrder maint) {
		MaintainTrace trace = new MaintainTrace();
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		trace.setMaintainTraceId(generateCode("WT"));
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
		
	private String generateCode(String prefix) {
		String code = StringUtil.generateCode(prefix);
		//校验是否重复
		MaintainTrace trace = traceMapper.selectByPrimaryKey(code);
		if (trace != null) {
			code = generateCode(prefix);
		}
		return code;
	}
}
