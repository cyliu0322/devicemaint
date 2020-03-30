package com.maint.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.maint.common.exception.ResultException;
import com.maint.common.util.StringUtil;
import com.maint.system.mapper.CompanyMapper;
import com.maint.system.mapper.DeviceMapper;
import com.maint.system.model.Company;

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
	
	public List<Company> selectAllWithQuery(int page, int rows, Company company) {
		PageHelper.startPage(page, rows);
		return companyMapper.selectAllWithQuery(company);
	}
	
	public List<Company> selectByKeyWord(String companyName) {
		Company company = new Company();
		company.setCompanyName(companyName);
		return companyMapper.selectAllWithQuery(company);
	}
	
	public Company selectOne(String companyId) {
		return companyMapper.selectByPrimaryKey(companyId);
	}
	
	@Transactional
	public int add(Company company) {
		checkCompanyNameExistOnCreate(company.getCompanyName());
		company.setCompanyId(generateCode("VP"));
		return companyMapper.insert(company);
	}
	
	/**
	 * 新增并返回主键
	 * @param company
	 * @return
	 */
	@Transactional
	public String insert(Company company) {
		checkCompanyNameExistOnCreate(company.getCompanyName());
		company.setCompanyId(generateCode("VP"));
		companyMapper.insert(company);
		return company.getCompanyId();
	}
	
	@Transactional
	public boolean update(Company company) {
		checkCompanyNameExistOnUpdate(company);
		return companyMapper.updateByPrimaryKey(company) == 1 ? true : false;
	}
	
	@Transactional
	public void delete(String companyId) {
		//删除其设备信息
		deviceService.delByCompanyId(companyId);
		companyMapper.deleteByPrimaryKey(companyId);
	}
	
	/**
	 * 新增时校验大客户名称是否重复
	 * @param companyName
	 */
	public void checkCompanyNameExistOnCreate(String companyName) {
		if (companyMapper.countByCompanyName(companyName) > 0) {
			throw new ResultException("大客户名称已存在");
		}
	}
	
	public void checkCompanyNameExistOnUpdate(Company company) {
		if (companyMapper.countByCompanyNameNotIncludeCompanyId(company.getCompanyName(), company.getCompanyId()) > 0) {
			throw new ResultException("大客户名称已存在");
		}
	}
	
	private String generateCode(String prefix) {
		String code = StringUtil.generateCode(prefix);
		//校验是否重复
		if (companyMapper.selectByPrimaryKey(code) != null) {
			code = generateCode(prefix);
		}
		return code;
	}
}