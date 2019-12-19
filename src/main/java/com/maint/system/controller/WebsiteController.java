package com.maint.system.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.maint.common.constants.LoginType;
import com.maint.common.shiro.token.UserPasswordToken;
import com.maint.common.util.PageResultBean;
import com.maint.common.util.ResultBean;
import com.maint.common.util.StringUtil;
import com.maint.system.mapper.WebUserMapper;
import com.maint.system.model.Company;
import com.maint.system.model.Device;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.OrderStatusBean;
import com.maint.system.model.WebUser;
import com.maint.system.service.WebUserService;

@Controller
@RequestMapping(value = "website")
public class WebsiteController {
	private static final Logger log = LoggerFactory.getLogger(MobileLoginController.class);
	
	@Resource
	WebUserService webUserService;
	
	@Autowired
	private WebUserMapper webUserMapper;
	
	@GetMapping(value = "/view/index")
	public String index() {
		return "website/index";
	}
	
	@GetMapping(value = "/view/news")
	public String news() {
		return "website/news";
	}
	
	@GetMapping(value = "/view/newsDetail")
	public String newsDetail() {
		return "website/newsDetail";
	}
	
	@GetMapping(value = "/view/toCase")
	public String toCase() {
		return "website/case";
	}
	
	@GetMapping(value = "/view/product")
	public String product() {
		return "website/product";
	}
	
	@GetMapping(value = "/view/about")
	public String about() {
		return "website/about";
	}
	
	@GetMapping(value = "/user/userCenter")
	public String userCenter() {
		return "website/userCenter";
	}
	
	@PostMapping(value="/view/isLogin")
	@ResponseBody
	public ResultBean isLogin() {
		WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();
		ResultBean resultBean = null;
		if (StringUtil.isNull(webUser)) {
			resultBean = ResultBean.error("未登录");
		}else {
			resultBean = ResultBean.success("已登录");
		}
		
		return resultBean;
	}
	
	@GetMapping(value = "/view/toLogin")
	public String toLogin() {
		return "website/login";
	}
	
	@GetMapping(value = "/view/toRegister")
	public String toRegister() {
		return "website/register";
	}
	
	@GetMapping(value = "/user/toUpdPwd")
	public String toUpdPwd() {
		return "website/updPwd";
	}
	
	@GetMapping(value = "/user/toOrderManage")
	public String toOrderManage() {
		return "website/orderManage";
	}
	
	@GetMapping(value = "/user/toOrderTrace")
	public String toOrderTrace(Model model,
			@RequestParam(value="orderId", required = true) String orderId) {
		
		List<OrderStatusBean> orderStatuss = webUserService.getOrderTraceStatus(orderId);
		
		model.addAttribute("orderStatuss", orderStatuss);
		
		return "website/orderTrace";
	}
	
	@GetMapping(value = "/user/toPersonInfo")
	public String toPersonInfo(Model model) {
		
		model.addAttribute("webUser", webUserService.getWebUser());
		
		return "website/personInfo";
	}
	
	@GetMapping(value = "/user/toRepairApplication")
	public String toRepairApplication(Model model) {
		
		model.addAttribute("brands", webUserService.getDeviceBrands());
		
		return "website/repairApplication";
	}
	
	@PostMapping(value="/view/login")
	@ResponseBody
	public ResultBean login(HttpServletRequest request) {
		
		Map<String, String[]> dataMap = request.getParameterMap();
		
		Subject subject = SecurityUtils.getSubject();
		UserPasswordToken usernamePasswordToken = new UserPasswordToken(
				dataMap.get("username")[0], 
				dataMap.get("password")[0].toCharArray(), 
				LoginType.WEB.toString());
		
		ResultBean resultBean = null;
		try {
			subject.login(usernamePasswordToken);
			resultBean = ResultBean.success("登录成功");
			
			WebUser webUser = (WebUser)SecurityUtils.getSubject().getPrincipal();
			webUserMapper.updateLastLoginTime(webUser.getWebUserId(), new Date());
		} catch (UnknownAccountException ae) {
			resultBean = ResultBean.error("用户名/密码错误");
		} catch (IncorrectCredentialsException ce) {
			resultBean = ResultBean.error("用户名/密码错误");
		} catch (LockedAccountException le) {
			resultBean = ResultBean.error("账号被锁定");
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@GetMapping("/user/logout")
	public String logout() {
		SecurityUtils.getSubject().logout();
		return "redirect:/website/view/toLogin";
	}
	
	@PostMapping(value="/view/register")
	@ResponseBody
	public ResultBean register(HttpServletRequest request) {
		
		Map<String, String[]> dataMap = request.getParameterMap();
		
		ResultBean resultBean = null;
		try {
			String addMsg = webUserService.addWebUser(dataMap);
			resultBean = ResultBean.success(addMsg);
			log.info(addMsg);
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value="/user/updPwd")
	@ResponseBody
	public ResultBean updPwd(HttpServletRequest request) {
		
		Map<String, String[]> dataMap = request.getParameterMap();
		
		ResultBean resultBean = null;
		try {
			String updMsg = webUserService.updPwd(dataMap);
			resultBean = ResultBean.success(updMsg);
			log.info(updMsg);
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value="/user/updPersonInfo")
	@ResponseBody
	public ResultBean updPersonInfo(HttpServletRequest request) {
		
		Map<String, String[]> dataMap = request.getParameterMap();
		
		ResultBean resultBean = null;
		try {
			String updMsg = webUserService.updPersonInfo(dataMap);
			
			resultBean = ResultBean.success(updMsg);
			log.info(updMsg);
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@GetMapping(value="/user/getOrders")
	@ResponseBody
	public PageResultBean<MaintainOrder> getOrders(HttpServletRequest request,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "limit", defaultValue = "10") int limit,
			@RequestParam(value = "orderId", defaultValue = "", required = false) String orderId,
			@RequestParam(value = "deviceName", defaultValue = "", required = false) String deviceName) {
		
		try {
			List<MaintainOrder> orders = webUserService.getOrders(page, limit, orderId, deviceName);
			
			PageInfo<MaintainOrder> orderPageInfo = new PageInfo<>(orders);
			return new PageResultBean<>(orderPageInfo.getTotal(), orderPageInfo.getList());
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	@PostMapping(value="/user/repairApplicationSave")
	@ResponseBody
	public ResultBean repairApplicationSave(HttpServletRequest request, @RequestBody String paramData) {
		
		System.out.println(paramData);
		
		ResultBean resultBean = null;
		try {
			String updMsg = webUserService.repairApplicationSave(paramData);
			
			resultBean = ResultBean.success(updMsg);
			log.info(updMsg);
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value="/user/getCompanies")
	@ResponseBody
	public ResultBean getCompanies() {
		
		ResultBean resultBean = null;
		try {
			List<Company> companies = webUserService.getCompanies();
			
			resultBean = ResultBean.success(companies);
			log.info(companies.toString());
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value="/user/getDevices")
	@ResponseBody
	public ResultBean getDevices(@RequestParam(value="companyId" ) String companyId) {
		
		ResultBean resultBean = null;
		try {
			List<Device> devices = webUserService.getDevices(companyId);
			
			resultBean = ResultBean.success(devices);
			log.info(devices.toString());
		}catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}

}
