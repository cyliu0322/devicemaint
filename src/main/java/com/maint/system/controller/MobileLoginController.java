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
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.OrderStatusBean;
import com.maint.system.service.MobileLoginService;

@Controller
@RequestMapping(value = "mobile")
public class MobileLoginController {
	
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
	
	/*
	 * @GetMapping(value = "toOrderDetail1") public String toOrderDetail(Model
	 * model,
	 * 
	 * @RequestParam(value = "orderType", required = true) String orderType) {
	 * model.addAttribute("orderType", orderType);
	 * 
	 * List<OrderStatusBean> orderStatusBeans = new ArrayList<OrderStatusBean>();
	 * //首检bean OrderStatusBean sjBean = new OrderStatusBean();
	 * sjBean.setOrderStatusDescription("已首检"); sjBean.setFaultCause("首检故障原因");
	 * sjBean.setDate("2019/11/24 14:08"); orderStatusBeans.add(sjBean);
	 * 
	 * //维修bean1 OrderStatusBean wxBean1 = new OrderStatusBean();
	 * wxBean1.setOrderStatusDescription("维修中"); wxBean1.setFaultCause("维修故障原因1");
	 * wxBean1.setDate("2019/11/25 14:08"); orderStatusBeans.add(wxBean1);
	 * 
	 * //维修bean2 OrderStatusBean wxBean2 = new OrderStatusBean();
	 * wxBean2.setOrderStatusDescription("维修中"); wxBean2.setFaultCause("维修故障原因2");
	 * wxBean2.setDate("2019/11/25 14:08"); orderStatusBeans.add(wxBean2);
	 * 
	 * //维修bean OrderStatusBean wcBean = new OrderStatusBean();
	 * wcBean.setOrderStatusDescription("已完成"); wcBean.setFaultCause("");
	 * wcBean.setDate("2019/11/26 14:08"); orderStatusBeans.add(wcBean);
	 * 
	 * model.addAttribute("orderStatuss",orderStatusBeans);
	 * 
	 * return "mobile/order-detail"; }
	 */
	
	@GetMapping(value = "goMaintain")
	public String goMaintain(Model model,
			@RequestParam(value = "orderId", required = true) String orderId,
			@RequestParam(value = "orderType", required = true) String orderType,
			@RequestParam(value = "deviceBrand", required = true) String deviceBrand) {
		
		return null;
	}
	
	@GetMapping(value = "toOrderDetail")
	public String toOrderDetail(Model model,
			@RequestParam(value = "orderId", required = true) String orderId,
			@RequestParam(value = "orderType", required = true) String orderType) {
		
		try {
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
			e.getStackTrace();
		}
		
		return "mobile/order-detail";
	}
	
	@PostMapping(value = "searchOrder")
	@ResponseBody
	public String searchOrder(
			@RequestParam(value = "order_id", required = false) String order_id) {
		
		ResultBean resultBean = null;
		try {
//			List<OrderAssign> orderAssigns = mobileService.<OrderAssign>searchOrderService(order_id, flag);
//			resultBean = ResultBean.successData(orderAssigns);
		} catch (Exception e) {
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
			System.out.println("文件上传失败，文件为空。");
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
		return "";
	}
	

}
