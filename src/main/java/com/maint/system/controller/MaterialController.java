package com.maint.system.controller;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.model.Material;
import com.maint.system.service.MaterialService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/material")
public class MaterialController {

	@Resource
	private MaterialService materialService;
	
	@GetMapping("/index")
	public String index() {
		return "material/material-list";
	}
	
	@OperationLog("获取耗材列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<Material> getList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, Material materialQuery) {
		List<Material> materials = materialService.selectAllWithQuery(page, limit, materialQuery);
		PageInfo<Material> pageInfo = new PageInfo<>(materials);
		return new PageResultBean<>(pageInfo.getTotal(), pageInfo.getList());
	}
	
	@GetMapping
	public String add(Model model) {
		return "material/material-add";
	}
	
	@GetMapping("/{materialId}")
	public String update(@PathVariable("materialId") String materialId, Model model) {
		model.addAttribute("material", materialService.selectOne(materialId));
		return "material/material-add";
	}
	
	@OperationLog("编辑耗材")
	@PutMapping
	@ResponseBody
	public ResultBean update(Material material) {
		materialService.update(material);
		return ResultBean.success();
	}
	
	@OperationLog("新增耗材")
	@PostMapping
	@ResponseBody
	public ResultBean add(Material material) {
		return ResultBean.success(materialService.add(material));
	}
	
	@OperationLog("删除耗材")
	@DeleteMapping("/{materialId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("materialId") String materialId) {
		materialService.delete(materialId);
		return ResultBean.success();
	}
}