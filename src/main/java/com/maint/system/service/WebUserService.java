package com.maint.system.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.maint.common.util.DateUtils;
import com.maint.common.util.StringUtil;
import com.maint.common.util.UUID19;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.mapper.DeviceBrandMapper;
import com.maint.system.mapper.MaintStepTraceMapper;
import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.mapper.WebUserMapper;
import com.maint.system.model.DeviceBrand;
import com.maint.system.model.MaintStepTrace;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.OrderStatusBean;
import com.maint.system.model.WebUser;

@Service
public class WebUserService {
	private static final Logger log = LoggerFactory.getLogger(WebUserService.class);
	
	@Autowired
	private WebUserMapper webUserMapper;
	@Autowired
	private MaintainOrderMapper maintainOrderMapper;
	@Autowired
	private DeviceBrandMapper deviceBrandMapper;
	@Autowired
	private MaintainTraceMapper maintainTraceMapper;
	@Autowired
	private MaintStepTraceMapper maintStepTraceMapper;
	
	public WebUser getWebUserByUserNameOrPhone(String userNameOrPhone) {
		return webUserMapper.selectWebUserByUserNameOrPhone(userNameOrPhone);
	}
	
	public String addWebUser(Map<String, String[]> dataMap) throws Exception {
		
		String usernameFlag = webUserMapper.isExistUsername(dataMap.get("username")[0]);
		if ("1".equals(usernameFlag)) {
			throw new Exception("用户名已经存在，请换一个");
		}
		
		String phoneFlag = webUserMapper.isExistPhone(dataMap.get("phone")[0]);
		if ("1".equals(phoneFlag)) {
			throw new Exception("该手机号已经注册，可直接登录");
		}
		
		String salt = String.valueOf(System.currentTimeMillis());
		String encryptPassword = new Md5Hash(dataMap.get("password")[0], salt).toString();
		
		WebUser webUser = new WebUser();
		webUser.setUsername(dataMap.get("username")[0]);
		webUser.setTel(dataMap.get("phone")[0]);
		webUser.setPassword(encryptPassword);
		webUser.setSalt(salt);
		webUser.setNickname(dataMap.get("username")[0]);
		webUser.setStatus(1);
		webUserMapper.insertSelective(webUser);
		return "账号注册成功";
	}
	
	public String updPwd(Map<String, String[]> dataMap) throws Exception {
		
		WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();
		String oldPssword = new Md5Hash(dataMap.get("oldPassword")[0], webUser.getSalt()).toString();
		if (!webUser.getPassword().equals(oldPssword)) {
			throw new Exception("输入的原密码不正确");
		}
		
		String salt = String.valueOf(System.currentTimeMillis());
		String encryptPassword = new Md5Hash(dataMap.get("newPassword")[0], salt).toString();
		webUserMapper.updatePwd(webUser.getWebUserId(), encryptPassword, salt);
		
		return "密码修改成功";
	}
	
	public String updPersonInfo(Map<String, String[]> dataMap) throws Exception {
		
		WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();
		webUser.setCompanyAddress(dataMap.get("companyAddress")[0]);
		webUser.setCompanyContact(dataMap.get("companyContact")[0]);
		webUser.setCompanyEmail(dataMap.get("companyEmail")[0]);
		webUser.setCompanyName(dataMap.get("companyName")[0]);
		webUser.setCompanyPhone(dataMap.get("companyPhone")[0]);
		webUser.setEmail(dataMap.get("email")[0]);
		webUser.setModifyTime(new Date());
		webUser.setNickname(dataMap.get("nickname")[0]);
		webUser.setTel(dataMap.get("tel")[0]);
		webUser.setUsername(dataMap.get("username")[0]);
		
		webUserMapper.updateByPrimaryKeySelective(webUser);
		
		return "个人信息修改成功";
	}
	
	public WebUser getWebUser() {
		WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();
		
		return webUserMapper.selectByPrimaryKey(webUser.getWebUserId());
	}
	
	public List<MaintainOrder> getOrders(int page, int rows, String orderId, String deviceName){
		
		PageHelper.startPage(page, rows);
		
		WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("webUserId", webUser.getWebUserId());
		paraMap.put("orderId", orderId);
		paraMap.put("deviceName", deviceName);
		
		return maintainOrderMapper.selectOrderByConditions(paraMap);
		
	}
	
	public String repairApplicationSave(MaintainOrder order) {
		
		//判断用户是否录入了企业信息
		
		
		MaintainOrder maintainOrder = order;
		maintainOrder.setMaintainOrderId("WX"+UUID19.uuid().substring(0, 18));
		WebUser webUser = (WebUser) SecurityUtils.getSubject().getPrincipal();
		maintainOrder.setWebUserId(webUser.getWebUserId());
		maintainOrder.setState(MaintainOrderStatusEnum.WXSQ.getValue());
		maintainOrder.setCreateDate(new Date());
		maintainOrderMapper.insert(maintainOrder);
		
		return "维修申请成功";
	}
	
	public List<DeviceBrand> getDeviceBrands(){
		
		return deviceBrandMapper.selectAllWithQuery(new DeviceBrand());
	}
	
	public List<OrderStatusBean> getOrderTraceStatus(String orderId){
		
		List<MaintainTrace> maintainTraces = maintainTraceMapper.selectOrderTraceByOrderId(orderId);
		List<OrderStatusBean> orderStatuss = new ArrayList<OrderStatusBean>();
		
		maintainTraces.forEach(maintainTrace -> {
			OrderStatusBean orderStatusBean = new OrderStatusBean();
			orderStatusBean.setDate(DateUtils.parseDateToStr("yyyy/MM/dd HH:mm:ss", maintainTrace.getMaintainDate()));
			orderStatusBean.setFaultCause(maintainTrace.getFaultCause());
			orderStatusBean.setOrderStatusDescription(
					MaintainOrderStatusEnum.getvalueOf(maintainTrace.getOrderStatus()).getTxt());
			if(MaintainOrderStatusEnum.WXZ.getValue().equals(maintainTrace.getOrderStatus())) {
				//获取维修步骤信息
				List<MaintStepTrace> maintStepTraces = maintStepTraceMapper.selectMaintStepTracesByOrderTreaceId(maintainTrace.getMaintainTraceId());
				if (!StringUtil.isNull(maintStepTraces)) {
					orderStatusBean.setMaintStepTraces(maintStepTraces);
				}
			}
			
			orderStatuss.add(orderStatusBean);
		});
		
		return orderStatuss;
	}

}
