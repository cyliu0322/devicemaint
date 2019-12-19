package com.maint.system.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maint.common.annotation.OperationLog;
import com.maint.common.util.ResultBean;
import com.maint.system.model.Device;
import com.maint.system.service.BrandService;
import com.maint.system.service.DeviceService;

@Controller
@RequestMapping("/device")
public class DeviceController {

	@Resource
	private DeviceService deviceService;
	
	@Resource
	private BrandService brandService;
	
	@GetMapping
	public String add(Model model) {
		model.addAttribute("brands", brandService.selectAll());
		return "vip/device-add";
	}
	
	@GetMapping("/{deviceId}")
	public String update(@PathVariable("deviceId") String deviceId, Model model) {
		model.addAttribute("device", deviceService.selectOne(deviceId));
		model.addAttribute("brands", brandService.selectAll());
		return "vip/device-add";
	}
	
	@GetMapping("/list/{companyId}")
	@ResponseBody
	public ResultBean selectDeviceByVip(@PathVariable("companyId") String companyId) {
		return ResultBean.success(deviceService.selectByCompanyId(companyId));
	}
	
	@GetMapping("/{companyId}/keyword")
	@ResponseBody
	public ResultBean selectByKeyword(@PathVariable("companyId") String companyId, @RequestParam(defaultValue = "") String keyword) {
		return ResultBean.success(deviceService.selectByCompanyIdAndKeyword(companyId, keyword));
	}
	
	@OperationLog("新增设备")
	@PostMapping
	@ResponseBody
	public ResultBean add(Device device) {
		return ResultBean.success(deviceService.add(device));
	}
	
	@OperationLog("编辑设备")
	@PutMapping
	@ResponseBody
	public ResultBean update(Device device) {
		deviceService.update(device);
		return ResultBean.success();
	}
	
	@OperationLog("删除设备")
	@DeleteMapping("/{deviceId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("deviceId") String deviceId) {
		deviceService.delete(deviceId);
		return ResultBean.success();
	}
}