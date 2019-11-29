package com.maint.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.model.Dept;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.PointDetail;
import com.maint.system.model.User;
import com.maint.system.service.DeptService;
import com.maint.system.service.MaintService;
import com.maint.system.service.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 维修单业务
 * @author aisino
 *
 */
@Controller
@RequestMapping("/maint")
public class MaintController {

	@Resource
	private MaintService maintService;
	
	@Resource
	private UserService userService;
	
	@GetMapping("/index")
	public String index() {
		return "maint/maint-list";
	}
	
	@OperationLog("获取维修单列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<MaintainOrder> getMaintList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit) {
		List<MaintainOrder> maints = maintService.selectAllMaint();
		PageInfo<MaintainOrder> maintPageInfo = new PageInfo<>(maints);
		return new PageResultBean<>(maintPageInfo.getTotal(), maintPageInfo.getList());
	}
	
	@GetMapping("/trace/{maintId}")
	@ResponseBody
	public ResultBean getTraceList(@PathVariable("maintId") String maintId) {
		return ResultBean.success(maintService.selectTracesByMaintId(maintId));
	}
	
	@GetMapping("/prior/{maintId}")
	public String appointPrior(@PathVariable("maintId") String maintId, Model model) {
		List<User> users = userService.selectPriorers();
		model.addAttribute("users", users);
		model.addAttribute("maintId", maintId);
		model.addAttribute("appoint", MaintainOrderStatusEnum.DSJ.getValue());
		return "maint/maint-prior";
	}
	
	@GetMapping("/mp/{maintId}")
	public String appointMp(@PathVariable("maintId") String maintId, Model model) {
		List<User> users = userService.selectPriorers();
		model.addAttribute("users", users);
		model.addAttribute("maintId", maintId);
		model.addAttribute("appoint", MaintainOrderStatusEnum.DWX.getValue());
		return "maint/maint-mp";
	}
	
	@PutMapping("/appoint")
	@ResponseBody
	public ResultBean update(MaintainOrder maint) {
		maintService.appoint(maint);
		return ResultBean.success();
	}
	
}
