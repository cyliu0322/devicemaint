package com.maint.system.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.maint.common.constants.LoginType;
import com.maint.common.shiro.token.UserPasswordToken;
import com.maint.common.util.ResultBean;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.model.MaintStepAidBean;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.MaterialAidBean;
import com.maint.system.model.OrderAidBean;
import com.maint.system.model.OrderStatusBean;
import com.maint.system.service.MobileLoginService;

@Controller
@RequestMapping(value = "mobile")
public class MobileLoginController {
	private static final Logger log = LoggerFactory.getLogger(MobileLoginController.class);
	
	@Resource
	MobileLoginService mobileService;
	
	@GetMapping(value = "test")
	public String test() {
		return "mobile/testUploadByLayui";
	}
	
	@GetMapping(value = "toLogin")
	public String toLogin() {
		return "mobile/login";
	}
	
	@GetMapping(value = "toUploadPicture")
	public String toUploadPicture() {
		return "mobile/test";
	}
	
	@GetMapping(value = "toSearchOrder")
	public String toSearchOrder() {
		return "mobile/search-order";
	}
	
	@GetMapping(value = "goMaintain")
	public String goMaintain(Model model,
			@RequestParam(value = "orderId", required = true) String orderId,
			@RequestParam(value = "deviceBrand", required = true) String deviceBrand,
			@RequestParam(value = "state", required = false) String state) {
		
		model.addAttribute("orderId", orderId);
		
		if (StringUtil.isNotEmpty(state) && MaintainOrderStatusEnum.DSJ.getValue().equals(state)) {
			return "mobile/first-inspection";
		}
		
		model.addAttribute("deviceBrand", deviceBrand);
		return "mobile/operate-order";
	}
	
	@GetMapping(value = "toOrderDetail")
	public String toOrderDetail(Model model,
			@RequestParam(value = "orderId", required = true) String orderId) {
		
		try {
			String orderType = mobileService.getOrderTypeByOrderId(orderId).getValue();
			List<OrderStatusBean> orderStatusBeans = mobileService.getOrderTraceInfo(orderId, orderType);
			
			//获取订单详情
			if(OrderTypeEnum.WX.getValue().equals(orderType)) {
				MaintainOrder maintainOrder = mobileService.<MaintainOrder>getOrderInfo(orderId, orderType);
				model.addAttribute("order", maintainOrder);
			}else {
				MaintenanceOrder maintenanceOrder = mobileService.<MaintenanceOrder>getOrderInfo(orderId, orderType);
				model.addAttribute("order", maintenanceOrder);
			}
			
			model.addAttribute("orderType", orderType);
			model.addAttribute("orderStatuss", orderStatusBeans);
			
		} catch (Exception e) {
			log.error("订单号："+orderId+"Error :"+e.getMessage());
			e.getStackTrace();
		}
		
		return "mobile/order-detail";
	}
	
	@PostMapping(value = "searchOrder")
	@ResponseBody
	public String searchOrder(
			@RequestParam(value = "order_id", required = false) String orderId) {
		
		ResultBean resultBean = null;
		try {
			List<OrderAidBean> orders = mobileService.<OrderAidBean>searchOrderService(
					orderId, 
					mobileService.getOrderTypeByOrderId(orderId).getValue());
			
			resultBean = ResultBean.successData(orders);
		} catch (Exception e) {
			log.error("订单号："+orderId+"Error :"+e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return JSONObject.toJSONString(resultBean);
	}
	
	@PostMapping(value = "login")
	@ResponseBody
	public String login(@RequestBody String data) {
		
		JSONObject jsonParam = JSONObject.parseObject(data);
		
		Subject subject = SecurityUtils.getSubject();
		UserPasswordToken usernamePasswordToken = new UserPasswordToken(
				jsonParam.getString("mobileAccount"), 
				jsonParam.getString("mobilePassword").toCharArray(), 
				LoginType.MOBILE.toString());
		
		ResultBean resultBean = null;
		try {
			subject.login(usernamePasswordToken);
			resultBean = ResultBean.success("登录成功");
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
		
		//更改最后登录时间，待做
		
		return JSONObject.toJSONString(resultBean);
	}
	
	@PostMapping(value = "uploadFile")
	@ResponseBody
	public ResultBean uploadFile(HttpServletRequest resquest,
			@RequestParam("file") MultipartFile file) {
		
		Map<String, String[]> resquestMap = resquest.getParameterMap();
		
		ResultBean resultBean = null;
		try {
			
			String fileName = mobileService.uploadFile(file, resquestMap);
			
			resultBean = ResultBean.success(fileName);
		} catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		return resultBean;
	}
	
	@PostMapping(value = "getMetrials")
	@ResponseBody
	public String getMetrials(HttpServletRequest resquest) {
		
		ResultBean resultBean = null;
		try {
			List<MaterialAidBean> materials = mobileService.getMaterials();
			resultBean = ResultBean.successData(materials);
		} catch (Exception e) {
			log.error(e.getMessage());
			ResultBean.error(e.getMessage());
		}
		
		String reString = JSONObject.toJSONString(resultBean);
		
		return reString;
	}
	
	@PostMapping(value = "saveFirstInspection")
	@ResponseBody
	public String saveFirstInspection(HttpServletRequest resquest) {
		
		Map<String, String[]> dataMap =resquest.getParameterMap();
		
		ResultBean resultBean = null;
		try {
			mobileService.saveFirstInspection(dataMap);
			resultBean = ResultBean.success("首检资料保存成功！");
		} catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return JSONObject.toJSONString(resultBean);
	}
	
	@GetMapping(value = "getBrandStep")
	@ResponseBody
	public ResultBean getBrandStep(HttpServletRequest resquest) {
		
		Map<String, String[]> dataMap =resquest.getParameterMap();
		
		ResultBean resultBean = null;
		try {
			List<MaintStepAidBean> steps = mobileService.getBrandStep(dataMap);
			resultBean = ResultBean.success(steps);
		} catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	@PostMapping(value = "saveMaintInfo")
	@ResponseBody
	public ResultBean saveMaintInfo(HttpServletRequest resquest) {
		
		Map<String, String[]> dataMap =resquest.getParameterMap();
		
		dataMap.forEach((k,v)->{
			System.out.println("key="+k+",value="+v[0]);
		});
		
		ResultBean resultBean = null;
		try {
			mobileService.saveMaintInfo(dataMap);
			resultBean = ResultBean.success("维修信息保存成功");
		} catch (Exception e) {
			log.error(e.getMessage());
			resultBean = ResultBean.error(e.getMessage());
		}
		
		return resultBean;
	}
	
	
}
