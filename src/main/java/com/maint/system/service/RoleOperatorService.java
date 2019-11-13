package com.maint.system.service;

import org.springframework.stereotype.Service;

import com.maint.system.mapper.RoleOperatorMapper;
import com.maint.system.model.RoleOperator;

import javax.annotation.Resource;

@Service
public class RoleOperatorService {

    @Resource
    private RoleOperatorMapper roleOperatorMapper;

    public int insert(RoleOperator roleOperator) {
        return roleOperatorMapper.insert(roleOperator);
    }

}
