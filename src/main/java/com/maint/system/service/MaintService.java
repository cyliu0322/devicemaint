package com.maint.system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;

import javax.annotation.Resource;

import java.util.List;

@Service
public class MaintService {

	@Resource
	private MaintainOrderMapper maintMapper;
	
	@Resource
	private MaintainTraceMapper traceMapper;
	
	public List<MaintainOrder> selectAllMaint() {
		return maintMapper.selectAllMaint();
	}
	
	public List<MaintainTrace> selectTracesByMaintId(String maintId) {
		return traceMapper.selectByMaintId(maintId);
	}
}
