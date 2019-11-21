package com.maint.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.maint.common.annotation.OperationLog;
import com.maint.common.util.ResultBean;
import com.maint.system.model.Dept;
import com.maint.system.model.PointDetail;
import com.maint.system.service.DeptService;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/dept")
public class DeptController {

	@Resource
	private DeptService deptService;
	
	@GetMapping("/index")
	public String index() {
		return "dept/dept-list";
	}
	
//	@OperationLog("获取部门列表")
//	@GetMapping("/list")
//	@ResponseBody
//	public ResultBean getList(@RequestParam(required = false) Integer parentId) {
//		List<Dept> deptList = deptService.selectByParentId(parentId);
//		return ResultBean.success(deptList);
//	}
	@OperationLog("获取区域列表")
	@GetMapping("/area/list")
	@ResponseBody
	public ResultBean getAreaList(@RequestParam(required = false) Integer parentId) {
		List<Dept> deptList = deptService.selectAreaByParentId(parentId);
		return ResultBean.success(deptList);
	}
	@OperationLog("获取区域列表")
	@GetMapping("/point/list")
	@ResponseBody
	public ResultBean getPointList(@RequestParam(required = false) Integer parentId) {
		List<Dept> deptList = deptService.selectPointByParentId(parentId);
		return ResultBean.success(deptList);
	}
	
	@GetMapping("/tree/root")
	@ResponseBody
	public ResultBean treeAndRoot() {
		return ResultBean.success(deptService.selectAllDeptTreeAndRoot());
	}
	
	/**
	 * 区域树
	 * @return
	 */
	@GetMapping("/tree/area")
	@ResponseBody
	public ResultBean areaTree() {
		ResultBean rb = ResultBean.success(deptService.selectAllAreaTreeAndRoot());
		return ResultBean.success(deptService.selectAllAreaTreeAndRoot());
	}
	
	@GetMapping("/tree")
	@ResponseBody
	public ResultBean tree() {
		return ResultBean.success(deptService.selectAllDeptTree());
	}
	
//	@GetMapping
//	public String add() {
//		return "dept/dept-add";
//	}
	@GetMapping("/area")
	public String addArea() {
		return "dept/dept-add";
	}
	
	@GetMapping("/point")
	public String addPoint() {
		return "dept/point-add";
	}
	
	@GetMapping("/detail/{deptId}")
	public String detail(@PathVariable("deptId") Integer deptId, Model model) {
		PointDetail detail = deptService.selectDetailByDeptId(deptId);
		model.addAttribute("detail", detail);
		return "dept/dept-detail";
	}
	
	@OperationLog("新增区域")
	@PostMapping
	@ResponseBody
	public ResultBean add(Dept dept) {
		return ResultBean.success(deptService.insert(dept));
	}
	
	@OperationLog("删除部门")
	@DeleteMapping("/{deptId}")
	@ResponseBody
	public ResultBean delete(@PathVariable("deptId") Integer deptId) {
		deptService.deleteCascadeByID(deptId);
		return ResultBean.success();
	}
	
	@OperationLog("修改部门")
	@PutMapping
	@ResponseBody
	public ResultBean update(Dept dept) {
		deptService.updateByPrimaryKey(dept);
		return ResultBean.success();
	}
	
	@GetMapping("/{deptId}")
	public String update(@PathVariable("deptId") Integer deptId, Model model) {
		Dept dept = deptService.selectByPrimaryKey(deptId);
		model.addAttribute("dept", dept);
		return "dept/dept-add";
	}
	
	@OperationLog("调整部门排序")
	@PostMapping("/swap")
	@ResponseBody
	public ResultBean swapSort(Integer currentId, Integer swapId) {
		deptService.swapSort(currentId, swapId);
		return ResultBean.success();
	}

}
