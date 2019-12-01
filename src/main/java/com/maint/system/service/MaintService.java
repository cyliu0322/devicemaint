package com.maint.system.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.maint.common.util.StringUtil;
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
	
	public List<MaintainOrder> selectAllMaint() {
		return maintMapper.selectAllMaint();
	}
	
	public MaintainOrder selectMaintById(String maintId) {
		return maintMapper.selectByPrimaryKey(maintId);
	}
	
	public List<MaintainTrace> selectTracesByMaintId(String maintId) {
		return traceMapper.selectByMaintId(maintId);
	}
	
	@Transactional
	public boolean appoint(MaintainOrder maint) {
		MaintainTrace trace = new MaintainTrace();
		//当前用户
		User currentUser = (User) SecurityUtils.getSubject().getPrincipal();
		trace.setMaintainTraceId(generateCode(10));
		trace.setMaintainOrderId(maint.getMaintainOrderId());
		trace.setOrderStatus(maint.getState());
		trace.setUserId(currentUser.getUserId());
		
		traceMapper.insert(trace);
		
		return maintMapper.updateByPrimaryKeySelective(maint) == 1 ? true : false;
		
		//TODO
		//短信发送服务
	}
	
	private String generateCode(int length) {
		String code = StringUtil.generateCode(length);
		//校验是否重复
		MaintainTrace trace = traceMapper.selectByPrimaryKey(code);
		if (trace != null) {
			code = generateCode(length);
		}
		return code;
	}
}
