package com.maint.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.constants.RoleEnum;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.User;
import com.maint.system.model.vo.MaintVO;
import com.maint.system.service.BrandService;
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
	
	@Resource
	private BrandService brandService;
	
	@GetMapping("/index")
	public String index() {
		return "maint/maint-list";
	}
	
	@GetMapping("/star")
	public String star() {
		return "maint/star";
	}
	
	@OperationLog("获取维修单列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<MaintainOrder> getMaintList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, MaintainOrder maintQuery) {
		List<MaintainOrder> maints = maintService.selectAllWithQuery(page, limit, maintQuery);
		PageInfo<MaintainOrder> maintPageInfo = new PageInfo<>(maints);
		return new PageResultBean<>(maintPageInfo.getTotal(), maintPageInfo.getList());
	}
	
	@GetMapping
	public String add(Model model) {
		model.addAttribute("brands", brandService.selectAll());
		return "maint/maint-add";
	}
	
	@GetMapping("/trace/{maintId}")
	@ResponseBody
	public ResultBean getTraceList(@PathVariable("maintId") String maintId) {
		return ResultBean.success(maintService.selectTracesByMaintId(maintId));
	}
	
	@GetMapping("/prior/{maintId}")
	public String appointPrior(@PathVariable("maintId") String maintId, Model model) {
		List<User> users = userService.selectByRole(RoleEnum.SJY.getRoleId());
		MaintainOrder maint = maintService.selectByMaintId(maintId);
		model.addAttribute("users", users);
		model.addAttribute("maint", maint);
		model.addAttribute("nextState", MaintainOrderStatusEnum.DSJ.getValue());
		return "maint/maint-prior";
	}
	
	@GetMapping("/mp/{maintId}")
	public String appointMp(@PathVariable("maintId") String maintId, Model model) {
		MaintainOrder maint = maintService.selectByMaintId(maintId);
		model.addAttribute("maint", maint);
		model.addAttribute("nextState", MaintainOrderStatusEnum.DWX.getValue());
		return "maint/maint-mp";
	}
	
	@OperationLog("新增维修单")
	@PostMapping
	@ResponseBody
	public ResultBean add(MaintVO maintVO) {
		return ResultBean.success(maintService.add(maintVO));
	}
	
	@PutMapping("/appoint")
	@ResponseBody
	public ResultBean appoint(MaintainOrder maint) {
		maintService.appoint(maint);
		return ResultBean.success();
	}
	
	@OperationLog("删除维修单")
	@DeleteMapping("/{maintId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("maintId") String maintId) {
		maintService.delete(maintId);
		return ResultBean.success();
	}
}
