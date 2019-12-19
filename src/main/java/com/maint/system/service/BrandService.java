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
import com.maint.system.model.DeviceBrand;
import com.maint.system.model.Step;
import com.maint.system.model.vo.BrandStepVO;

import java.util.ArrayList;
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
	public int add(BrandStepVO brandStepVO) {
		int mIndex = brandStepVO.getMaintIndex();
		int kIndex = brandStepVO.getKeepIndex();
		
		checkBrandNameExistOnCreate(brandStepVO.getBrandName());
		
		String brandId = generateCode("BD");	//品牌Id
		
		// 维修流程
		if (mIndex > 0) {	//维修流程有内容
			int num = 1;
			List<String> maints = brandStepVO.getMaintSteps();
			for (int i = 0; i < mIndex; i++) {	//小于mIndex表示后续空步骤不记录
				Step step = new Step();
				step.setStepId("W" + brandId + num);
				step.setStepName(maints.get(i));
				step.setBrandId(brandId);
				step.setStepType(0);
				step.setWeight(num);
				
				stepMapper.insert(step);
				num++;
			}
		}
		
		// 保养流程
		if (kIndex > 0) {	//保养流程有内容
			int num = 1;
			List<String> keeps = brandStepVO.getKeepSteps();
			for (int i = 0; i < kIndex; i++) {
				Step step = new Step();
				step.setStepId("B" + brandId + num);
				step.setStepName(keeps.get(i));
				step.setBrandId(brandId);
				step.setStepType(1);
				step.setWeight(num);
				
				stepMapper.insert(step);
				num++;
			}
		}
		
		// 品牌信息
		DeviceBrand brand = new DeviceBrand();
		brand.setBrandId(brandId);
		brand.setBrandName(brandStepVO.getBrandName());
		
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
	
	@Transactional
	public int add(String brandId, int type, BrandStepVO brandAndStep) {
		int maxWeight = stepMapper.selectMaxWeight(brandId, type);
		List<String> steps = new ArrayList<String>();
		int index;
		int result = 0;
		switch (type) {
			case 0:
				steps = brandAndStep.getMaintSteps();
				index = brandAndStep.getMaintIndex();
				for (int i = 0; i < index; i++) {
					Step step = new Step();
					step.setStepId("W" + brandId + (maxWeight + 1));
					step.setStepName(steps.get(i));
					step.setBrandId(brandId);
					step.setStepType(0);
					step.setWeight(maxWeight + 1);
					
					stepMapper.insert(step);
					
					result++;
					maxWeight++;
				}
				break;
			case 1:
				steps = brandAndStep.getKeepSteps();
				index = brandAndStep.getKeepIndex();
				for (int i = 0; i < index; i++) {
					Step step = new Step();
					step.setStepId("B" + brandId + (maxWeight + 1));
					step.setStepName(steps.get(i));
					step.setBrandId(brandId);
					step.setStepType(1);
					step.setWeight(maxWeight + 1);
					
					stepMapper.insert(step);
					
					result++;
					maxWeight++;
				}
				break;
			default:
				break;
		}
		
		return result;
	}
	
	@Transactional
	public boolean update(Step step) {
		return stepMapper.updateByPrimaryKeySelective(step) == 1;
	}
	
	@Transactional
	public void deleteStep(String stepId) {
		stepMapper.deleteByPrimaryKey(stepId);
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
	
	private String generateCode(String prefix) {
		String code = StringUtil.generateCode(prefix);
		//校验是否重复
		if (brandMapper.selectByPrimaryKey(code) != null) {
			code = generateCode(prefix);
		}
		return code;
	}	
}