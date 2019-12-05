package com.maint.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.maint.system.mapper.DeviceBrandMapper;
import com.maint.system.model.DeviceBrand;

import java.util.List;

import javax.annotation.Resource;

@Service
public class BrandService {

	private static final Logger log = LoggerFactory.getLogger(BrandService.class);
	
	@Resource
	private DeviceBrandMapper brandMapper;
	
	public DeviceBrand selectOne(String brandId) {
		return brandMapper.selectByPrimaryKey(brandId);
	}
	
	public List<DeviceBrand> selectAll() {
		return brandMapper.selectAllWithQuery(null);
	}
	
	public List<DeviceBrand> selectAllWithQuery(int page, int rows, DeviceBrand brand) {
		PageHelper.startPage(page, rows);
		return brandMapper.selectAllWithQuery(brand);
	}
	
	@Transactional
	public int add(DeviceBrand brand) {
		return 0;
	}
	
	@Transactional
	public boolean update(DeviceBrand brand) {
		return false;
	}
	
	@Transactional
	public void delete(String brandId) {
		
	}
}