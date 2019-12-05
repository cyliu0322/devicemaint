package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.maint.system.model.MaintStepTrace;

public interface MaintStepTraceMapper {
	
	@Select(value="SELECT id FROM tbl_maint_step_trace WHERE order_id = #{orderId} AND step_id = #{stepId}")
	String selectId(@Param("orderId") String orderId, @Param("stepId") String stepId);
	
	@Update(value="UPDATE tbl_maint_step_trace SET is_complete = #{isComplete} WHERE id = #{id}")
	int updateIsComplete(@Param("id") String id, @Param("isComplete") String isComplete);
	
	@Select(value="SELECT * FROM tbl_maint_step_trace WHERE order_id = #{orderId}")
	List<MaintStepTrace> selectMaintStepTracesByOrderId(@Param("orderId") String orderId);
	
	@Select(value="SELECT * FROM tbl_maint_step_trace WHERE order_id = #{orderId} AND step_id = #{stepId}")
	MaintStepTrace selectMaintStepTracesByStepId(@Param("orderId") String orderId, @Param("stepId") String stepId);
	
    int insert(MaintStepTrace record);

    int insertSelective(MaintStepTrace record);
}