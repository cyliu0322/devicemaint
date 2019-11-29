package com.maint.system.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
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
import com.maint.common.util.ResultBean;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.OrderAidBean;
import com.maint.system.model.OrderStatusBean;
import com.maint.system.service.MobileLoginService;

@Controller
@RequestMapping(value = "mobile")
public class MobileLoginController {
	private static final Logger log = LoggerFactory.getLogger(MobileLoginController.class);
	
	@Resource
	MobileLoginService mobileService;
	
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
		
		System.out.println("orderId:"+orderId+", deviceBrand:"+deviceBrand+",state:"+state);
		
		if (StringUtil.isNotEmpty(state) && MaintainOrderStatusEnum.DSJ.getValue().equals(state)) {
			return "mobile/first-inspection";
		}
		
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
		UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(
				jsonParam.getString("mobileAccount"), jsonParam.getString("mobilePassword"));
		
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
			resultBean = ResultBean.error(e.getMessage());
		}
		
		//更改最后登录时间，待做
		
		return JSONObject.toJSONString(resultBean);
	}
	
	@PostMapping(value = "uploadPicture")
	@ResponseBody
	public String uploadPicture(HttpServletRequest resquest,
			@RequestParam("file") MultipartFile file) {
		
		Map<String, String[]> resquestMap = resquest.getParameterMap();
		
		if(file.isEmpty()) {
			log.error("文件上传失败，文件为空。");
		}
		try {
			file.transferTo(new File("E:\\个人\\项目\\维修系统\\测试图片上传"
		+File.separator+resquestMap.get("uuid")[0]+
		file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."))));
			System.out.println("文件上传成功，original file name : "+file.getOriginalFilename()+" , "
					+ "new file name : "+resquestMap.get("uuid")[0]+
					file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resquestMap.forEach((k,v) -> {
			System.out.println("key : "+k+",value : "+v);
		});
		System.out.println("----uploadPicture-------");
		return resquestMap.get("uuid")[0]+
				file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
	}
	

}
