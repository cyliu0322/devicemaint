package com.maint.system.controller;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.model.DeviceBrand;
import com.maint.system.service.BrandService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
	public String add() {
		return "brand/brand-add";
	}
	
	@GetMapping("/{brandId}")
	public String update(@PathVariable("brandId") String brandId, Model model) {
		model.addAttribute("brand", brandService.selectOne(brandId));
		return "brand/brand-add";
	}
	
	@OperationLog("新增品牌")
	@PostMapping
	@ResponseBody
	public ResultBean add(DeviceBrand brand) {
		return ResultBean.success(brandService.add(brand));
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

}