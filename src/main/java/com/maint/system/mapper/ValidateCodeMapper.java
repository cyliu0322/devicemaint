package com.maint.system.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.ValidateCode;

public interface ValidateCodeMapper {
	@Delete("DELETE FROM tbl_validate_code WHERE tel_email=#{tel}")
	int deleteByTel(@Param("tel") String tel);
	
	@Select("SELECT '1' FROM tbl_validate_code WHERE tel_email=#{tel} AND yzm=#{yzm}")
	String selectByTelAndYzm(@Param("tel") String tel, @Param("yzm") String yzm);
	
    int insert(ValidateCode record);

    int insertSelective(ValidateCode record);
}