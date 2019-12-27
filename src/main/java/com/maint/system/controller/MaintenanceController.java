package com.maint.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.enums.MaintenanceOrderStatusEnum;
import com.maint.system.model.Company;
import com.maint.system.model.Device;
import com.maint.system.model.DeviceBrand;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.service.BrandService;
import com.maint.system.service.DeviceService;
import com.maint.system.service.MaintenanceService;
import com.maint.system.service.VipService;

import java.util.List;

import javax.annotation.Resource;

/**
 * 保养单业务
 * @author aisino
 *
 */
@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

	@Resource
	private DeviceService deviceService;
	
	@Resource
	private VipService vipService;
	
	@Resource
	private BrandService brandService;
	
	@Resource
	private MaintenanceService maintenanceService;
	
	@GetMapping("/index")
	public String index() {
		return "maintenance/maintenance-list";
	}
	
	@GetMapping("/grade")
	public String grade() {
		return "maintenance/maintenance-grade";
	}
	
	@OperationLog("获取保养单列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<MaintenanceOrder> getMaintenanceList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, MaintenanceOrder maintenanceQuery) {
		List<MaintenanceOrder> maintenances = maintenanceService.selectAllWithQuery(page, limit, maintenanceQuery);
		PageInfo<MaintenanceOrder> pageInfo = new PageInfo<>(maintenances);
		return new PageResultBean<>(pageInfo.getTotal(), pageInfo.getList());
	}
	
	@GetMapping("/{deviceId}")
	public String add(@PathVariable("deviceId") String deviceId, Model model) {
		Device device = deviceService.selectOne(deviceId);
		Company company = vipService.selectOne(device.getCompanyId());
		DeviceBrand brand = brandService.selectOne(device.getBrandId());
		model.addAttribute("device", device);
		model.addAttribute("vip", company);
		model.addAttribute("brand", brand);
		return "vip/upkeep-add";
	}
	
	@OperationLog("新增保养单")
	@PostMapping
	@ResponseBody
	public ResultBean add(MaintenanceOrder maintenance) {
		maintenanceService.add(maintenance);
		return ResultBean.success();
	}
	
	@GetMapping("/trace/{maintenanceId}")
	@ResponseBody
	public ResultBean getTraceList(@PathVariable("maintenanceId") String maintenanceId) {
		return ResultBean.success(maintenanceService.getTracesByMaintenanceId(maintenanceId));
	}
	
	@GetMapping("/appoint/{maintenanceId}")
	public String appoint(@PathVariable("maintenanceId") String maintenanceId, Model model) {
		MaintenanceOrder maintenance = maintenanceService.selectByMaintenanceId(maintenanceId);
		model.addAttribute("maintenance", maintenance);
		model.addAttribute("nextState", MaintenanceOrderStatusEnum.DBY.getValue());
		return "maintenance/maintenance-appoint";
	}
	
	@PutMapping("/appoint")
	@ResponseBody
	public ResultBean appoint(MaintenanceOrder maintenance) {
		maintenanceService.appoint(maintenance);
		return ResultBean.success();
	}
	
	@OperationLog("删除保养单")
	@DeleteMapping("/{maintenanceId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("maintenanceId") String maintenanceId) {
		maintenanceService.delete(maintenanceId);
		return ResultBean.success();
	}
}
