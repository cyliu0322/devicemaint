package com.maint.common.shiro.realm;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.maint.common.shiro.ShiroActionProperties;
import com.maint.common.util.ShiroUtil;
import com.maint.system.model.User;
import com.maint.system.service.UserService;

@Component
public class MobileRealm extends AuthorizingRealm{
	private static final Logger log = LoggerFactory.getLogger(ManageRealm.class);

    @Resource
    private UserService userService;

    @Resource
    private ShiroActionProperties shiroActionProperties;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    	log.info("手机端用户认证");
        String username = (String) token.getPrincipal();
        User user = userService.selectOneByUserName(username);
        if (user == null) {
            throw new UnknownAccountException();
        }
        // 如果账号被锁定, 则抛出异常, (超级管理员除外)
        if (ShiroUtil.USER_LOCK.equals(user.getStatus()) && !shiroActionProperties.getSuperAdminUsername().equals(username)) {
            throw new LockedAccountException();
        }
        return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), super.getName());
    }

}
