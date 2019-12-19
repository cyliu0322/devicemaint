package com.maint.system.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.maint.system.model.Device;

public interface DeviceMapper {
	/**
	 * 更改设备保养时间
	 * @param deviceId 设备id
	 * @return
	 */
	@Update("UPDATE tbl_device SET last_maintenance_time=#{lastMaintenanceTime} WHERE device_id=#{deviceId}")
	int updateLastMaintenanceTime(@Param("deviceId") String deviceId, @Param("lastMaintenanceTime") Date lastMaintenanceTime);
	
    int deleteByPrimaryKey(String deviceId);

    int insert(Device record);

    int insertSelective(Device record);

    Device selectByPrimaryKey(String deviceId);
    
    List<Device> selectByCompanyId(@Param("companyId") String companyId);
    
    List<Device> selectByCompanyIdAndKeyword(@Param("companyId") String companyId, @Param("keyword") String keyword);
    
    int delByCompanyId(@Param("companyId") String companyId);

    int updateByPrimaryKeySelective(Device record);

    int updateByPrimaryKey(Device record);
}