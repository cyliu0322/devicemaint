package com.maint.system.service;

import com.github.pagehelper.PageHelper;
import com.maint.common.exception.DuplicateNameException;
import com.maint.common.util.StringUtil;
import com.maint.system.mapper.MaterialMapper;
import com.maint.system.model.Material;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;

@Service
public class MaterialService {

	private static final Logger log = LoggerFactory.getLogger(MaterialService.class);
	
	@Resource
	private MaterialMapper materialMapper;
	
	public List<Material> selectAllWithQuery(int page, int rows, Material materialQuery) {
		PageHelper.startPage(page, rows);
		List<Material> materials = materialMapper.selectAllWithQuery(materialQuery);
		
		return materials;
	}
	
	@Transactional
	public Integer add(Material material) {
		checkMaterialNameExistOnCreate(material.getMaterialName());
		material.setMaterialId(generateCode(10));
		return materialMapper.insert(material);
	}
	
	@Transactional
	public boolean update(Material material) {
		checkMaterialNameExistOnUpdate(material);
		return materialMapper.updateByPrimaryKeySelective(material) == 1;
	}
	
	public Material selectOne(String id) {
		return materialMapper.selectByPrimaryKey(id);
	}
	
	@Transactional
	public void delete(String materialId) {
		materialMapper.deleteByPrimaryKey(materialId);
	}
	
	/**
	 * 新增时校验材料名称是否重复
	 * @param materialName
	 */
	public void checkMaterialNameExistOnCreate(String materialName) {
		if (materialMapper.countByMaterialName(materialName) > 0) {
			throw new DuplicateNameException("材料名称已存在");
		}
	}
	
	public void checkMaterialNameExistOnUpdate(Material material) {
		if (materialMapper.countByMaterialNameNotIncludeMaterialId(material.getMaterialName(), material.getMaterialId()) > 0) {
			throw new DuplicateNameException("材料名称已存在");
		}
	}
	
	private String generateCode(int length) {
		String code = StringUtil.generateCode(length, "");
		//校验是否重复
		if (materialMapper.selectByPrimaryKey(code) != null) {
			code = generateCode(length);
		}
		return code;
	}
}