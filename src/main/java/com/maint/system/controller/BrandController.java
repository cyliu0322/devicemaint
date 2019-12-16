package com.maint.system.controller;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.model.DeviceBrand;
import com.maint.system.model.Step;
import com.maint.system.model.BrandAndStep;
import com.maint.system.service.BrandService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandController {

	@Resource
	private BrandService brandService;
	
	@GetMapping("/index")
	public String index() {
		return "brand/brand-list";
	}
	
	@OperationLog("获取品牌列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<DeviceBrand> getList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, DeviceBrand brandQuery) {
		List<DeviceBrand> brands = brandService.selectAllWithQuery(page, limit, brandQuery);
		PageInfo<DeviceBrand> brandPageInfo = new PageInfo<>(brands);
		return new PageResultBean<>(brandPageInfo.getTotal(), brandPageInfo.getList());
	}
	
	@GetMapping
	public String add(Model model) {
		//Form表单绑定对象 必须初始化值
		BrandAndStep brandAndStep = new BrandAndStep();
		List<String> maints = new ArrayList<String>();
		List<String> keeps = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			maints.add("");
			keeps.add("");
		}
		brandAndStep.setBrandName("");
		brandAndStep.setMaintSteps(maints);
		brandAndStep.setKeepSteps(keeps);
		model.addAttribute("brandAndStep", brandAndStep);
		return "brand/brand-add";
	}
	
	@GetMapping("/{brandId}")
	public String update(@PathVariable("brandId") String brandId, Model model) {
		model.addAttribute("brand", brandService.selectOne(brandId));
		return "brand/brand-edit";
	}
	
	@OperationLog("新增品牌")
	@PostMapping
	@ResponseBody
	public ResultBean add(@ModelAttribute(value = "brandAndStep") BrandAndStep brandAndStep) {
		return ResultBean.success(brandService.add(brandAndStep));
	}
	
	@OperationLog("编辑品牌")
	@PutMapping
	@ResponseBody
	public ResultBean update(DeviceBrand brand) {
		brandService.update(brand);
		return ResultBean.success();
	}
	
	@OperationLog("删除品牌")
	@DeleteMapping("/{brandId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("brandId") String brandId) {
		brandService.delete(brandId);
		return ResultBean.success();
	}
	
	//---------------------------------------------------
	//********************维修及保养流程*********************
	//---------------------------------------------------
	
	@GetMapping("/step/{brandId}/{type}")
	@ResponseBody
	public ResultBean getSteps(@PathVariable("brandId") String brandId, @PathVariable("type") int type) {
		return ResultBean.success(brandService.getStepsByBrandIdAndType(brandId, type));
	}
	
	@GetMapping("/step/{brandId}/{type}/{rowNum}")
	public String addStep(@PathVariable("brandId") String brandId, @PathVariable("type") Integer type,
			@PathVariable("rowNum") Integer rowNum, Model model) {
		String res = null;
		//Form表单绑定对象 必须初始化值
		BrandAndStep brandAndStep = new BrandAndStep();
		List<String> steps = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			steps.add("");
		}
		switch (type) {
			case 0:
				brandAndStep.setMaintSteps(steps);
				res = "brand/step-add";
				break;
			case 1:
				brandAndStep.setKeepSteps(steps);
				res = "brand/step-add2";
				break;
			default:
				break;
		}
		model.addAttribute("brandAndStep", brandAndStep);
		model.addAttribute("brandId", brandId);
		model.addAttribute("rowNum", rowNum);
		
		return res;
	}
	
	@GetMapping("/step/edit/{type}/{stepId}/{index}")
	public String updateStep(@PathVariable("type") Integer type, @PathVariable("stepId") String stepId,
			@PathVariable("index") Integer index, Model model) {
		model.addAttribute("step", brandService.selectStepById(stepId));
		model.addAttribute("type", type == 0 ? "维修" : "保养");
		model.addAttribute("index", index + 1);
		return "brand/step-edit";
	}
	

	@OperationLog("添加流程")
	@PostMapping("/step/add/{brandId}/{type}")
	@ResponseBody
	public ResultBean add(@PathVariable("brandId") String brandId, @PathVariable("type") Integer type,
			BrandAndStep brandAndStep) {
		return ResultBean.success(brandService.add(brandId, type, brandAndStep));
	}
	
	@OperationLog("编辑流程")
	@PutMapping("/step")
	@ResponseBody
	public ResultBean update(Step step) {
		brandService.update(step);
		return ResultBean.success();
	}
	
	@OperationLog("删除流程")
	@DeleteMapping("/step/{stepId}")
	@ResponseBody
	public ResultBean deleteStep(@PathVariable("stepId") String stepId) {
		brandService.deleteStep(stepId);
		return ResultBean.success();
	}
	
	@OperationLog("调整维修/保养步骤排序")
    @PostMapping("/step/swap")
    @ResponseBody
    public ResultBean swapSort(String currentId, String swapId) {
		brandService.swapSort(currentId, swapId);
		return ResultBean.success();
	}
	
}