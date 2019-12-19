package com.maint.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maint.common.util.StringUtil;
import com.maint.system.mapper.DeviceMapper;
import com.maint.system.model.Device;

import java.util.List;

import javax.annotation.Resource;

@Service
public class DeviceService {

	private static final Logger log = LoggerFactory.getLogger(DeviceService.class);
	
	@Resource
	private DeviceMapper deviceMapper;
	
	public List<Device> selectByCompanyId(String companyId) {
		return deviceMapper.selectByCompanyId(companyId);
	}
	
	public List<Device> selectByCompanyIdAndKeyword(String companyId, String keyword) {
		return deviceMapper.selectByCompanyIdAndKeyword(companyId, keyword);
	}
	
	public Device selectOne(String deviceId) {
		return deviceMapper.selectByPrimaryKey(deviceId);
	}
	
	@Transactional
	public int add(Device device) {
		device.setDeviceId(generateCode("DV"));
		return deviceMapper.insert(device);
	}
	
	@Transactional
	public boolean update(Device device) {
		return deviceMapper.updateByPrimaryKeySelective(device) == 1 ? true : false;
	}
	
	@Transactional
	public void delete(String deviceId) {
		deviceMapper.deleteByPrimaryKey(deviceId);
	}
	
	@Transactional
	public void delByCompanyId(String companyId) {
		deviceMapper.delByCompanyId(companyId);
	}
	
	private String generateCode(String prefix) {
		String code = StringUtil.generateCode(prefix);
		//校验是否重复
		if (deviceMapper.selectByPrimaryKey(code) != null) {
			code = generateCode(prefix);
		}
		return code;
	}
}