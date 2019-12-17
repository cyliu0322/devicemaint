package com.maint.system.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maint.common.util.DateUtils;
import com.maint.common.util.FileUtil;
import com.maint.common.util.ShiroUtil;
import com.maint.common.util.StringUtil;
import com.maint.common.util.UUID19;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.enums.MaintenanceOrderStatusEnum;
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.mapper.DeviceMapper;
import com.maint.system.mapper.MaintStepTraceMapper;
import com.maint.system.mapper.MaintainMaterialMapper;
import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.mapper.MaintenanceMaterialMapper;
import com.maint.system.mapper.MaintenanceOrderMapper;
import com.maint.system.mapper.MaintenanceTraceMapper;
import com.maint.system.mapper.MaterialMapper;
import com.maint.system.mapper.StepMapper;
import com.maint.system.model.MaintStepAidBean;
import com.maint.system.model.MaintStepTrace;
import com.maint.system.model.MaintainMaterial;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.MaintenanceMaterial;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.MaintenanceTrace;
import com.maint.system.model.Material;
import com.maint.system.model.MaterialAidBean;
import com.maint.system.model.OrderAidBean;
import com.maint.system.model.OrderStatusBean;
import com.maint.system.model.Step;

@Service
public class MobileLoginService {
	
	@Autowired
	private MaintainTraceMapper maintainTraceMapper;
	@Autowired
	private MaintenanceTraceMapper maintenanceTraceMapper;
	@Autowired
	private MaintenanceOrderMapper maintenanceOrderMapper;
	@Autowired
	private MaintainOrderMapper maintainOrderMapper;
	@Autowired
	private MaterialMapper materialMapper;
	@Autowired
	private MaintainMaterialMapper maintainMaterialMapper;
	@Autowired
	private MaintenanceMaterialMapper maintenanceMaterialMapper;
	@Autowired
	private StepMapper stepMapper;
	@Autowired
	private MaintStepTraceMapper maintStepTraceMapper;
	@Autowired
	private DeviceMapper deviceMapper;
	
	@Value("${file.picture.save-url}")
	private String pictureSaveUrl;
	@Value("${file.video.save-url}")
	private String videoSaveUrl;
	
	@Transactional(rollbackFor=Exception.class)
	public void saveMaintInfo(Map<String, String[]> dataMap) throws Exception {
		String orderId = dataMap.get("orderId")[0];
		OrderTypeEnum orderTypeEnum = getOrderTypeByOrderId(orderId);
		String state = "";
		Date currentDate = new Date();
		if (OrderTypeEnum.WX.getValue().equals(orderTypeEnum.getValue())) {
			//维修
			state = "1".equals(dataMap.get("isSuccess")[0])?
					MaintainOrderStatusEnum.WXWC.getValue():MaintainOrderStatusEnum.WXZ.getValue();
			//插入追踪表（tbl_maintenance_trace）
			MaintainTrace maintainTrace = new MaintainTrace();
			maintainTrace.setFaultCause(dataMap.get("faultDescription")[0]);
			maintainTrace.setImage(dataMap.get("uploadedPictureName")[0]);
			maintainTrace.setMaintainDate(currentDate);
			String maintainTraceId = UUID19.uuid();
			maintainTrace.setMaintainOrderId(orderId);
			maintainTrace.setMaintainTraceId(maintainTraceId);
			maintainTrace.setOrderStatus(state);
			maintainTrace.setUserId(ShiroUtil.getCurrentUser().getUserId());
			maintainTrace.setVideo(dataMap.get("uploadedVideoName")[0]);
			maintainTraceMapper.insert(maintainTrace);
			
			//插入维修步骤追踪表（tbl_maint_step_trace），如果该步骤已经插入，就判断该步骤是否完成，如完成就更改is_complete字段
			String maintStepTraces = dataMap.get("brandStepTable")[0];
			JSONArray maintStepTraceJsonArray = JSONArray.parseArray(maintStepTraces);
			maintStepTraceJsonArray.forEach(maintStepTraceJson -> {
				JSONObject maintStepJsonObject = (JSONObject) maintStepTraceJson;
				MaintStepTrace maintStepTrace =maintStepTraceMapper.selectMaintStepTracesByStepId(orderId, maintStepJsonObject.getString("stepId"));
				String maintStepTraceId = "";
				if (StringUtil.isNull(maintStepTrace)) {
					maintStepTrace = new MaintStepTrace();
					maintStepTrace.setCreateDate(currentDate);
					maintStepTraceId = UUID19.uuid();
					maintStepTrace.setId(maintStepTraceId);
					maintStepTrace.setOrderId(orderId);
					maintStepTrace.setOrderTraceId(maintainTraceId);
					maintStepTrace.setStepId(maintStepJsonObject.getString("stepId"));
					maintStepTrace.setStepName(maintStepJsonObject.getString("stepName"));
					if("1".equals(maintStepJsonObject.getString("isComplete"))) {
						maintStepTrace.setIsComplete("1");
						maintStepTrace.setCompleteDate(currentDate);
					}
					maintStepTraceMapper.insert(maintStepTrace);
				}else {
					maintStepTraceId = maintStepTrace.getId();
					if ("0".equals(maintStepTrace.getIsComplete()) && "1".equals(maintStepJsonObject.getString("isComplete"))) {
						maintStepTraceMapper.updateIsComplete(maintStepTrace.getId(), "1");
					}
				}
			});
			
			//插入维修材料表（tbl_maintain_material）
			String materialsJson = dataMap.get("materials")[0];
			if (StringUtil.isNotEmpty(materialsJson)) {
				JSONArray jsonArray = JSONArray.parseArray(materialsJson);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject materialJson = (JSONObject) jsonArray.get(i);
					MaintainMaterial maintainMaterial = new MaintainMaterial();
					maintainMaterial.setMaintainMaterialId(UUID19.uuid());
					maintainMaterial.setMaterialId(materialJson.getString("materialId"));
					maintainMaterial.setMaintainOrderId(orderId);
					maintainMaterial.setAmount(Double.parseDouble(materialJson.getString("materialAmount")));
					maintainMaterial.setMaintainTraceId(maintainTraceId);
					maintainMaterial.setMaintStepTraceId(
							maintStepTraceMapper.selectId(
									orderId, materialJson.getString("modelStepId")));
					
					maintainMaterialMapper.insert(maintainMaterial);
				}
			}
			
			//更改订单表（tbl_maintain_order）状态
			if (state.equals(MaintainOrderStatusEnum.WXWC.getValue())) {
				maintainOrderMapper.updateState(orderId, state);
			}
		}else {
			//保养
			state = "1".equals(dataMap.get("isSuccess")[0])?
					MaintenanceOrderStatusEnum.BYWC.getValue():MaintenanceOrderStatusEnum.BYZ.getValue();
			//插入追踪表（tbl_maintenance_trace）
			MaintenanceTrace maintenanceTrace = new MaintenanceTrace();
			maintenanceTrace.setFaultCause(dataMap.get("faultDescription")[0]);
			maintenanceTrace.setImage(dataMap.get("uploadedPictureName")[0]);
			maintenanceTrace.setMaintenanceDate(currentDate);
			String maintenanceTraceId = UUID19.uuid();
			maintenanceTrace.setMaintenanceOrderId(orderId);
			maintenanceTrace.setMaintenanceTraceId(maintenanceTraceId);
			maintenanceTrace.setOrderStatus(state);
			maintenanceTrace.setUserId(ShiroUtil.getCurrentUser().getUserId());
			maintenanceTrace.setVideo(dataMap.get("uploadedVideoName")[0]);
			maintenanceTraceMapper.insert(maintenanceTrace);
			
			//插入维修步骤追踪表（tbl_maint_step_trace），如果该步骤已经插入，就判断该步骤是否完成，如完成就更改is_complete字段
			String maintStepTraces = dataMap.get("brandStepTable")[0];
			JSONArray maintStepTraceJsonArray = JSONArray.parseArray(maintStepTraces);
			maintStepTraceJsonArray.forEach(maintStepTraceJson -> {
				JSONObject maintStepJsonObject = (JSONObject) maintStepTraceJson;
				MaintStepTrace maintStepTrace =maintStepTraceMapper.selectMaintStepTracesByStepId(orderId, maintStepJsonObject.getString("stepId"));
				String maintStepTraceId = "";
				if (StringUtil.isNull(maintStepTrace)) {
					maintStepTrace = new MaintStepTrace();
					maintStepTrace.setCreateDate(currentDate);
					maintStepTraceId = UUID19.uuid();
					maintStepTrace.setId(maintStepTraceId);
					maintStepTrace.setOrderId(orderId);
					maintStepTrace.setOrderTraceId(maintenanceTraceId);
					maintStepTrace.setStepId(maintStepJsonObject.getString("stepId"));
					maintStepTrace.setStepName(maintStepJsonObject.getString("stepName"));
					if("1".equals(maintStepJsonObject.getString("isComplete"))) {
						maintStepTrace.setIsComplete("1");
						maintStepTrace.setCompleteDate(currentDate);
					}
					maintStepTraceMapper.insert(maintStepTrace);
				}else {
					maintStepTraceId = maintStepTrace.getId();
					if ("0".equals(maintStepTrace.getIsComplete()) && "1".equals(maintStepJsonObject.getString("isComplete"))) {
						maintStepTraceMapper.updateIsComplete(maintStepTrace.getId(), "1");
					}
				}
			});
			
			//插入维护材料表（tbl_maintenance_material）
			String materialsJson = dataMap.get("materials")[0];
			if (StringUtil.isNotEmpty(materialsJson)) {
				JSONArray jsonArray = JSONArray.parseArray(materialsJson);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject materialJson = (JSONObject) jsonArray.get(i);
					MaintenanceMaterial maintenanceMaterial = new MaintenanceMaterial();
					maintenanceMaterial.setMaintenanceMaterialId(UUID19.uuid());
					maintenanceMaterial.setMaterialId(materialJson.getString("materialId"));
					maintenanceMaterial.setMaintenanceOrderId(orderId);
					maintenanceMaterial.setAmount(Double.parseDouble(materialJson.getString("materialAmount")));
					maintenanceMaterial.setMaintenanceTraceId(maintenanceTraceId);
					maintenanceMaterial.setMaintStepTraceId(
							maintStepTraceMapper.selectId(
									orderId, materialJson.getString("modelStepId")));
					
					maintenanceMaterialMapper.insert(maintenanceMaterial);
				}
			}
			
			//更改订单表（tbl_maintenance_order）状态
			if(state.equals(MaintenanceOrderStatusEnum.BYWC.getValue())) {
				maintenanceOrderMapper.updateState(orderId, state);
				
				//更改设备保养时间
				String deviceId = maintenanceOrderMapper.selectDeviceIdByOrderId(orderId);
				deviceMapper.updateLastMaintenanceTime(deviceId, currentDate);
			}
		}
	}
	
	public List<MaintStepAidBean> getBrandStep(Map<String, String[]> dataMap) throws Exception{
		String orderType = getOrderTypeByOrderId(dataMap.get("orderId")[0]).getValue();
		List<Step> steps = stepMapper.selectSteps(dataMap.get("deviceBrand")[0], Integer.parseInt(orderType));
		List<MaintStepTrace> maintStepTraces = maintStepTraceMapper.selectMaintStepTracesByOrderId(dataMap.get("orderId")[0]);
		
		List<MaintStepAidBean> maintStepAidBeans = new ArrayList<MaintStepAidBean>();
		steps.stream().forEach((step) -> {
			MaintStepAidBean maintStepAidBean = new MaintStepAidBean();
			maintStepAidBean.setStepId(step.getStepId());
			maintStepAidBean.setStepName(step.getStepName());
			maintStepAidBean.setIsMaint(false);
			maintStepAidBean.setIsComplete("0");
			
			for (MaintStepTrace maintStepTrace : maintStepTraces) {
				if (step.getStepId().equals(maintStepTrace.getStepId())) {
					maintStepAidBean.setIsMaint(true);
					maintStepAidBean.setIsComplete(maintStepTrace.getIsComplete());
				}
			}
			
			maintStepAidBeans.add(maintStepAidBean);
		});
		
		return maintStepAidBeans;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveFirstInspection(Map<String, String[]> dataMap) throws Exception {
		//1、建立事物
		//2、保存数据到追踪表
		MaintainTrace maintainTrace = new MaintainTrace();
		String maintainTraceId = UUID19.uuid();
		maintainTrace.setMaintainTraceId(maintainTraceId);
		maintainTrace.setMaintainOrderId(dataMap.get("orderId")[0]);
		maintainTrace.setUserId(ShiroUtil.getCurrentUser().getUserId());
		maintainTrace.setFaultCause(dataMap.get("faultDescription")[0]);
		maintainTrace.setImage(dataMap.get("uploadedPictureName")[0]);
		maintainTrace.setVideo(dataMap.get("uploadedVideoName")[0]);
		maintainTrace.setMaintainDate(new Date());
		maintainTrace.setOrderStatus(MaintainOrderStatusEnum.YSJ.getValue());
		maintainTraceMapper.insert(maintainTrace);
		
		//3、保存数据到耗材表
		String materialsJson = dataMap.get("materials")[0];
		if (StringUtil.isNotEmpty(materialsJson)) {
			JSONArray jsonArray = JSONArray.parseArray(materialsJson);
			jsonArray.forEach(material -> {
				JSONObject materialJson = (JSONObject) material;
				MaintainMaterial maintainMaterial = new MaintainMaterial();
				maintainMaterial.setMaintainMaterialId(UUID19.uuid());
				maintainMaterial.setMaterialId(materialJson.getString("materialId"));
				maintainMaterial.setMaintainOrderId(dataMap.get("orderId")[0]);
				maintainMaterial.setAmount(Double.parseDouble(materialJson.getString("materialAmount")));
				maintainMaterial.setMaintainTraceId(maintainTraceId);
				
				maintainMaterialMapper.insert(maintainMaterial);
			});
		}
		
		//4、更改维修单状态
		maintainOrderMapper.updateState(dataMap.get("orderId")[0], MaintainOrderStatusEnum.YSJ.getValue());
		
	}
	
	
	 
	public List<MaterialAidBean> getMaterials(){
		
		List<Material> materials = materialMapper.selectMaterials();
		List<MaterialAidBean> materialAidBeans = new ArrayList<MaterialAidBean>();
		materials.stream().forEach(material -> {
			MaterialAidBean materialAidBean = new MaterialAidBean();
			materialAidBean.setValue(material.getMaterialId());
			materialAidBean.setLabel(material.getMaterialName());
			materialAidBean.setCategory(material.getCategory());
			materialAidBean.setDescription(material.getDescription());
			materialAidBean.setGgxh(material.getGgxh());
			materialAidBean.setMaterialBrand(material.getMaterialName());
			materialAidBean.setMaterialNumber(material.getMaterialNumber());
			materialAidBean.setPrice(material.getPrice());
			materialAidBean.setUnit(material.getUnit());
			
			materialAidBeans.add(materialAidBean);
		});
		return materialAidBeans;
	}
	
	public String uploadFile(MultipartFile file, Map<String, String[]> resquestMap) throws Exception {
		
		String fileSaveURL = "";
		
		if("picture".equals(resquestMap.get("fileType")[0])) {
			//上传图片
			fileSaveURL = pictureSaveUrl;
		}else {
			//上传视频
			fileSaveURL = videoSaveUrl;
		}
		try {
			String newFileName = FileUtil.updateFileName(
					file.getOriginalFilename(), resquestMap.get("uuid")[0]);
			
			file.transferTo(
					new File(
							fileSaveURL+
							File.separator+
							newFileName
							));
			System.out.println("文件上传成功，original file name : "+file.getOriginalFilename()+" , "
								+ "new file name : "+newFileName);
			return newFileName;
		} catch (Exception e) {
			throw new Exception(file.getOriginalFilename()+"文件上传失败，"+e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrderInfo(String orderId, String orderType) {
		
		T order = null;
		
		if(OrderTypeEnum.WX.getValue().equals(orderType)) {
			order = (T) maintainOrderMapper.selectOrderByOrderId(orderId);
		}else {
			order = (T) maintenanceOrderMapper.selectOrderByOrderId(orderId);
		}
		
		return order;
	}
	
	/**
	 * 获取订单追踪信息
	 * @param orderId 订单号
	 * @param orderType 订单类型，0：维修订单，1：保养订单
	 * @return
	 */
	public List<OrderStatusBean> getOrderTraceInfo(String orderId, String orderType){
		List<OrderStatusBean> orderStatusBeans = new ArrayList<OrderStatusBean>();
		
		if(OrderTypeEnum.WX.getValue().equals(orderType)) {
			//维修单
			List<MaintainTrace> maintainTraces = maintainTraceMapper.selectOrderTraceByOrderId(orderId);
			
			maintainTraces.stream().forEachOrdered(maintainTrace -> {
				OrderStatusBean orderStatusBean = new OrderStatusBean();
				orderStatusBean.setOrderStatusDescription(
						MaintainOrderStatusEnum.getvalueOf(maintainTrace.getOrderStatus()).getTxt());
				orderStatusBean.setFaultCause(maintainTrace.getFaultCause());
				orderStatusBean.setDate(DateUtils.parseDateToStr("yyyy/MM/dd HH:mm:ss", maintainTrace.getMaintainDate()));
				
				orderStatusBeans.add(orderStatusBean);
			});
			
		}else if (OrderTypeEnum.BY.getValue().equals(orderType)) {
			//保养单
			List<MaintenanceTrace> maintenanceTraces = maintenanceTraceMapper.selectOrderTraceByOrderId(orderId);
			
			maintenanceTraces.stream().forEachOrdered(maintenanceTrace -> {
				OrderStatusBean orderStatusBean = new OrderStatusBean();
				orderStatusBean.setOrderStatusDescription(
						MaintainOrderStatusEnum.getvalueOf(maintenanceTrace.getOrderStatus()).getTxt());
				orderStatusBean.setFaultCause(maintenanceTrace.getFaultCause());
				orderStatusBean.setDate(DateUtils.parseDateToStr("yyyy/MM/dd HH:mm:ss", maintenanceTrace.getMaintenanceDate()));
				
				orderStatusBeans.add(orderStatusBean);
			});
		}
		
		return orderStatusBeans;
	}
	
	/**
	 * 搜索订单信息
	 * @param <E>
	 * @param order_id 订单号
	 * @param orderType 查询标志，为0则代表查询维修订单表，为1则代表查询保养订单表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> searchOrderService(String orderId, String orderType) throws Exception{
		List<E> orders = new ArrayList<E>();
		
		if(OrderTypeEnum.WX.getValue().equals(orderType)) {
			//维修单
			List<MaintainOrder> maintainOrders = maintainOrderMapper.selectOrder(orderId, ShiroUtil.getCurrentUser().getUserId());
			
			maintainOrders.stream().forEach(maintainOrder -> {
				OrderAidBean orderAidBean = new OrderAidBean();
				orderAidBean.setOrderId(orderId);
				orderAidBean.setOrderStatus(MaintainOrderStatusEnum.getvalueOf(maintainOrder.getState()).getTxt());
				orderAidBean.setOrderType(OrderTypeEnum.WX.getTxt());
				orderAidBean.setDeviceName(maintainOrder.getDeviceName());
				orderAidBean.setDeviceAddr(maintainOrder.getAddress());
				orders.add((E) orderAidBean);
			});
		}else {
			//保养单
			List<MaintenanceOrder> maintenanceOrders = maintenanceOrderMapper.selectOrder(orderId, ShiroUtil.getCurrentUser().getUserId());
			
			maintenanceOrders.stream().forEach(maintenanceOrder -> {
				OrderAidBean orderAidBean = new OrderAidBean();
				orderAidBean.setOrderId(orderId);
				orderAidBean.setOrderStatus(MaintenanceOrderStatusEnum.getvalueOf(maintenanceOrder.getState()).getTxt());
				orderAidBean.setOrderType(OrderTypeEnum.BY.getTxt());
				orderAidBean.setDeviceName(maintenanceOrder.getDeviceName());
				orderAidBean.setDeviceAddr(maintenanceOrder.getAddress());
				orders.add((E) orderAidBean);
			});
		}
		
		return orders;
	}
	
	/**
	 * 通过订单号获取订单类型，订单前两位为订单类型标志（“BY”：代表保养单，“WX”：代表维修单）
	 * @param orderId 订单号
	 * @return
	 * @throws Exception 
	 */
	public OrderTypeEnum getOrderTypeByOrderId(String orderId) throws Exception {
		try {
			String orderTypeFlag = orderId.substring(0, 2);
			if("BY".equals(orderTypeFlag)) {
				return OrderTypeEnum.BY;
			}else {
				return OrderTypeEnum.WX;
			}
		} catch (Exception e) {
			throw new Exception("解析订单号时出错，"+e.getMessage());
		}
	}

}
