package com.maint.system.controller;

import com.github.pagehelper.PageInfo;
import com.maint.common.annotation.OperationLog;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.system.model.Company;
import com.maint.system.model.Dept;
import com.maint.system.service.DeptService;
import com.maint.system.service.VipService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/vip")
public class VipController {

	@Resource
	private VipService vipService;
	
	@Resource
	private DeptService deptService;
	
	@GetMapping("/index")
	public String index() {
		return "vip/vip-list";
	}
	
	@OperationLog("获取大客户列表")
	@GetMapping("/list")
	@ResponseBody
	public PageResultBean<Company> getList(@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit, Company companyQuery) {
		List<Company> vips = vipService.selectAllWithQuery(page, limit, companyQuery);
		PageInfo<Company> userPageInfo = new PageInfo<>(vips);
		return new PageResultBean<>(userPageInfo.getTotal(), userPageInfo.getList());
	}
	
	@GetMapping
	public String add() {
		return "vip/vip-add";
	}
	
	@GetMapping("/{companyId}")
	public String update(@PathVariable("companyId") String companyId, Model model) {
		model.addAttribute("vip", vipService.selectOne(companyId));
		return "vip/vip-add";
	}
	
	@OperationLog("新增大客户")
	@PostMapping
	@ResponseBody
	public ResultBean add(Company company) {
		return ResultBean.success(vipService.add(company));
	}
	
	@OperationLog("编辑大客户")
	@PutMapping
	@ResponseBody
	public ResultBean update(Company company) {
		vipService.update(company);
		return ResultBean.success();
	}
	
	@OperationLog("删除大客户")
	@DeleteMapping("/{companyId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("companyId") String companyId) {
		vipService.delete(companyId);
		return ResultBean.success();
	}

}