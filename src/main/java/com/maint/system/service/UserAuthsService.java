package com.maint.system.service;

import org.springframework.stereotype.Service;

import com.maint.common.constants.AuthcTypeEnum;
import com.maint.system.mapper.UserAuthsMapper;
import com.maint.system.model.UserAuths;

import javax.annotation.Resource;

@Service
public class UserAuthsService {

    @Resource
    private UserAuthsMapper userAuthsMapper;

    public int deleteByPrimaryKey(Integer id) {
        return userAuthsMapper.deleteByPrimaryKey(id);
    }

    public int insert(UserAuths record) {
        return userAuthsMapper.insert(record);
    }

    public UserAuths selectByPrimaryKey(Integer id) {
        return userAuthsMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKey(UserAuths record) {
        return userAuthsMapper.updateByPrimaryKey(record);
    }

	public UserAuths selectOneByIdentityTypeAndUserId(AuthcTypeEnum authcTypeEnum, Integer userId){
		 return userAuthsMapper.selectOneByIdentityTypeAndUserId(authcTypeEnum.getDescription(), userId);
	}

    public UserAuths selectOneByIdentityTypeAndIdentifier(AuthcTypeEnum authcTypeEnum, String identifier){
        return userAuthsMapper.selectOneByIdentityTypeAndIdentifier(authcTypeEnum.getDescription(), identifier);
    }

    public int deleteByUserId(Integer userId) {
        return userAuthsMapper.deleteByUserId(userId);
    }
}

