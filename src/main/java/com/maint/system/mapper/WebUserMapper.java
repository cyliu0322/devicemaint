package com.maint.system.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.maint.system.model.WebUser;

public interface WebUserMapper {
	@Update("UPDATE tbl_web_user SET password=#{newPwd}, salt=#{salt} WHERE web_user_id=#{webUserId}")
	int updatePwd(@Param("webUserId") int webUserId, @Param("newPwd") String newPwd, @Param("salt") String salt);
	
	@Select("SELECT '1' FROM tbl_web_user WHERE username=#{username} limit 1")
	String isExistUsername(@Param("username") String username);
	
	@Select("SELECT '1' FROM tbl_web_user WHERE tel=#{phone} limit 1")
	String isExistPhone(@Param("phone") String phone);
	
	@Update("UPDATE tbl_web_user SET last_login_time=#{lastLoginTime} WHERE web_user_id=#{webUserId}")
	int updateLastLoginTime(@Param("webUserId") int webUserId ,@Param("lastLoginTime") Date lastLoginTime);
	
	@Select("SELECT * FROM tbl_web_user WHERE username=#{userNameOrPhone} OR tel=#{userNameOrPhone} limit 1")
	WebUser selectWebUserByUserNameOrPhone(@Param("userNameOrPhone") String userNameOrPhone);
	
    int deleteByPrimaryKey(Integer webUserId);

    int insert(WebUser record);

    int insertSelective(WebUser record);

    WebUser selectByPrimaryKey(Integer webUserId);

    int updateByPrimaryKeySelective(WebUser record);

    int updateByPrimaryKey(WebUser record);
}