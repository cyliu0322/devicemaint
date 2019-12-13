package com.maint.system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.maint.common.exception.DuplicateNameException;
import com.maint.common.util.StringUtil;
import com.maint.system.mapper.DeviceBrandMapper;
import com.maint.system.mapper.StepMapper;
import com.maint.system.model.BrandAndStep;
import com.maint.system.model.DeviceBrand;
import com.maint.system.model.Step;

import java.util.List;

import javax.annotation.Resource;

@Service
public class BrandService {

	private static final Logger log = LoggerFactory.getLogger(BrandService.class);
	
	@Resource
	private DeviceBrandMapper brandMapper;
	
	@Resource
	private StepMapper stepMapper;
	
	public DeviceBrand selectOne(String brandId) {
		return brandMapper.selectByPrimaryKey(brandId);
	}
	
	public Step selectStepById(String stepId) {
		return stepMapper.selectByPrimaryKey(stepId);
	}
	
	public List<DeviceBrand> selectAll() {
		return brandMapper.selectAllWithQuery(null);
	}
	
	public List<DeviceBrand> selectAllWithQuery(int page, int rows, DeviceBrand brand) {
		PageHelper.startPage(page, rows);
		return brandMapper.selectAllWithQuery(brand);
	}
	
	@Transactional
	public int add(BrandAndStep brandAndStep) {
		int mIndex = brandAndStep.getMaintIndex();
		int kIndex = brandAndStep.getKeepIndex();
		
		checkBrandNameExistOnCreate(brandAndStep.getBrandName());
		
		String brandId = generateCode(10);	//品牌Id
		
		// 维修流程
		if (mIndex > 0) {	//维修流程有内容
			int num = 1;
			List<String> maints = brandAndStep.getMaintSteps();
			for (int i = 0; i < mIndex; i++) {	//小于mIndex表示后续空步骤不记录
				Step step = new Step();
				step.setStepDescription(maints.get(i));
				step.setStepType(0);
				step.setWeight(num);
				step.setBrandId(brandId);
				step.setStepName(num + "");
				step.setStepId("wx" + brandId + num);
				
				stepMapper.insert(step);
				num++;
			}
		}
		
		// 保养流程
		if (kIndex > 0) {	//保养流程有内容
			int num = 1;
			List<String> keeps = brandAndStep.getKeepSteps();
			for (int i = 0; i < kIndex; i++) {
				Step step = new Step();
				step.setStepDescription(keeps.get(i));
				step.setStepType(1);
				step.setWeight(num);
				step.setBrandId(brandId);
				step.setStepName(num + "");
				step.setStepId("by" + brandId + num);
				
				stepMapper.insert(step);
				num++;
			}
		}
		
		// 品牌信息
		DeviceBrand brand = new DeviceBrand();
		brand.setBrandId(brandId);
		brand.setBrandName(brandAndStep.getBrandName());
		
		return brandMapper.insert(brand);
	}
	
	@Transactional
	public boolean update(DeviceBrand brand) {
		checkMaterialNameExistOnUpdate(brand);
		return brandMapper.updateByPrimaryKeySelective(brand) == 1;
	}
	
	@Transactional
	public void delete(String brandId) {
		//删除流程信息
		stepMapper.deleteByBrandId(brandId);
		
		brandMapper.deleteByPrimaryKey(brandId);
	}
	
	public List<Step> getStepsByBrandIdAndType(String brandId, int type) {
		return stepMapper.selectByBrandIdAndType(brandId, type);
	}
	
	public void swapSort(String currentId, String swapId) {
		stepMapper.swapSort(currentId, swapId);
	}
	
	/**
	 * 新增时校验品牌名称是否重复
	 * @param brandName
	 */
	public void checkBrandNameExistOnCreate(String brandName) {
		if (brandMapper.countByBrandName(brandName) > 0) {
			throw new DuplicateNameException("品牌名称已存在");
		}
	}
	
	public void checkMaterialNameExistOnUpdate(DeviceBrand brand) {
		if (brandMapper.countByBrandNameNotIncludeBrandId(brand.getBrandName(), brand.getBrandId()) > 0) {
			throw new DuplicateNameException("品牌名称已存在");
		}
	}
	
	private String generateCode(int length) {
		String code = StringUtil.generateCode(length, "");
		//校验是否重复
		if (brandMapper.selectByPrimaryKey(code) != null) {
			code = generateCode(length);
		}
		return code;
	}	
}