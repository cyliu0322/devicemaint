package com.maint.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maint.common.util.StringUtil;
import com.maint.system.mapper.CompanyMapper;
import com.maint.system.mapper.DeviceMapper;
import com.maint.system.model.Company;
import com.maint.system.model.Device;

import java.util.List;

import javax.annotation.Resource;

@Service
public class VipService {

	private static final Logger log = LoggerFactory.getLogger(VipService.class);
	
	@Resource
	private DeviceService deviceService;
	
	@Resource
	private CompanyMapper companyMapper;
	
	@Resource
	private DeviceMapper deviceMapper;
	
	public List<Company> selectAllCompany() {
		return companyMapper.selectAllCompany();
	}
	
	public List<Device> selectDeviceByCompanyId(String companyId) {
		return deviceMapper.selectDeviceByCompanyId(companyId);
	}
	
	public Company selectOne(String companyId) {
		return companyMapper.selectByPrimaryKey(companyId);
	}
	
	@Transactional
	public int add(Company company) {
		company.setCompanyId(generateCode(10));
		return companyMapper.insert(company);
	}
	
	@Transactional
	public boolean update(Company company) {
		return companyMapper.updateByPrimaryKey(company) == 1 ? true : false;
	}
	
	@Transactional
	public void delete(String companyId) {
		//删除其设备信息
		deviceService.delByCompanyId(companyId);
		companyMapper.deleteByPrimaryKey(companyId);
	}
	
	private String generateCode(int length) {
		String code = StringUtil.generateCode(length);
		//校验是否重复
		if (companyMapper.selectByPrimaryKey(code) != null) {
			code = generateCode(length);
		}
		return code;
	}
}