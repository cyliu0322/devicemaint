package com.maint.system.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maint.common.annotation.OperationLog;
import com.maint.common.util.ResultBean;
import com.maint.system.model.Device;
import com.maint.system.service.DeviceService;

import cn.hutool.crypto.Mode;

@Controller
@RequestMapping("/device")
public class DeviceController {

	@Resource
	private DeviceService deviceService;
	
	@GetMapping
	public String add() {
		return "vip/device-add";
	}
	
	@GetMapping("/{deviceId}")
	public String update(@PathVariable("deviceId") String deviceId, Model model) {
		model.addAttribute("device", deviceService.selectOne(deviceId));
		return "vip/device-add";
	}
	
	@GetMapping("/list/{companyId}")
	@ResponseBody
	public ResultBean selectDeviceByVip(@PathVariable("companyId") String companyId) {
		return ResultBean.success(deviceService.selectDeviceByCompanyId(companyId));
	}
	
	@OperationLog("新增设备")
	@PostMapping
	@ResponseBody
	public ResultBean add(Device device) {
		return ResultBean.success(deviceService.add(device));
	}
}