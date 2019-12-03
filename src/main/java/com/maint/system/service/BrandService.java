package com.maint.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.maint.system.mapper.DeviceBrandMapper;
import com.maint.system.model.DeviceBrand;

import javax.annotation.Resource;

@Service
public class BrandService {

	private static final Logger log = LoggerFactory.getLogger(BrandService.class);
	
	@Resource
	private DeviceBrandMapper brandMapper;
	
	public DeviceBrand selectOne(String brandId) {
		return brandMapper.selectByPrimaryKey(brandId);
	}
	
}