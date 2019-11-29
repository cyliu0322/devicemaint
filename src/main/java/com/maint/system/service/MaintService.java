package com.maint.system.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;

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
		trace.setMaintainTraceId(generateCode());
		trace.setMaintainOrderId(maint.getMaintainOrderId());
		trace.setOrderStatus(maint.getState());
		
		traceMapper.insert(trace);
		
		MaintainOrder maintNew = maintMapper.selectByPrimaryKey(maint.getMaintainOrderId());
		maintNew.setState(maint.getState());
		maintNew.setUserId(maint.getUserId());
		
		return maintMapper.updateByPrimaryKey(maintNew) == 1 ? true : false;
	}
	
	private String generateCode() {
		String str = "ABCDEFGHJKLMNPQRTVWY0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 12; i++) {
			int number = random.nextInt(str.length());
			sb.append(str.charAt(number));
		}
		
		String code = sb.toString();
		//校验是否重复
		MaintainTrace trace = traceMapper.selectByPrimaryKey(code);
		if (trace != null) {
			generateCode();
		}
		return code;
	}
}
