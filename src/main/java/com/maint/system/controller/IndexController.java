package com.maint.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maint.common.annotation.OperationLog;
import com.maint.system.model.Menu;
import com.maint.system.service.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class IndexController {

	@Resource
	private MenuService menuService;
	
	@Resource
	private LoginLogService loginLogService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private RoleService roleService;
	
	@Resource
	private SysLogService sysLogService;
	
	@Resource
	private UserOnlineService userOnlineService;
	
	// 后台管理登录成功后跳转
	@GetMapping(value = { "/", "/main" })
	public String index(Model model) {
		List<Menu> menuTreeVOS = menuService.selectCurrentUserMenuTree();
		model.addAttribute("menus", menuTreeVOS);
		return "index";
	}
	
	// 移动端登录成功后跳转
	@GetMapping(value = { "/mobileMain" })
	public String mobileIndex(Model model) {
		List<Menu> menuTreeVOS = menuService.selectCurrentUserMenuTree();
		model.addAttribute("menus", menuTreeVOS);
		return "404";
	}
	
	// 门户网站登录成功后跳转
	
	@OperationLog("访问我的桌面")
	@GetMapping("/welcome")
	public String welcome(Model model) {
		int userCount = userService.count();
		int roleCount = roleService.count();
		int menuCount = menuService.count();
		int loginLogCount = loginLogService.count();
		int sysLogCount = sysLogService.count();
		int userOnlineCount = userOnlineService.count();
		
		model.addAttribute("userCount", userCount);
		model.addAttribute("roleCount", roleCount);
		model.addAttribute("menuCount", menuCount);
		model.addAttribute("loginLogCount", loginLogCount);
		model.addAttribute("sysLogCount", sysLogCount);
		model.addAttribute("userOnlineCount", userOnlineCount);
		return "welcome";
	}
	
	@OperationLog("查看近七日登录统计图")
	@GetMapping("/weekLoginCount")
	@ResponseBody
	public List<Integer> recentlyWeekLoginCount() {
		return loginLogService.recentlyWeekLoginCount();
	}
}
