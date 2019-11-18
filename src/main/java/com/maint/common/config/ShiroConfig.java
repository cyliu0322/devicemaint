package com.maint.common.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

import com.maint.common.shiro.EnhanceModularRealmAuthenticator;
import com.maint.common.shiro.OAuth2Helper;
import com.maint.common.shiro.RestShiroFilterFactoryBean;
import com.maint.common.shiro.ShiroActionProperties;
import com.maint.common.shiro.credential.RetryLimitHashedCredentialsMatcher;
import com.maint.common.shiro.filter.OAuth2AuthenticationFilter;
import com.maint.common.shiro.filter.RestAuthorizationFilter;
import com.maint.common.shiro.filter.RestFormAuthenticationFilter;
import com.maint.common.shiro.realm.OAuth2GiteeRealm;
import com.maint.common.shiro.realm.OAuth2GithubRealm;
import com.maint.common.shiro.realm.UserNameRealm;
import com.maint.system.service.ShiroService;

import javax.annotation.Resource;
import javax.servlet.Filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Configuration
public class ShiroConfig {

	@Resource
	private OAuth2Helper oAuth2Helper;
	
	@Lazy
	@Resource
	private ShiroService shiroService;
	
	@Resource
	private ShiroActionProperties shiroActionProperties;
	
	@Value("${spring.redis.host}")
	private String redisHost;
	
	@Value("${spring.redis.port}")
	private Integer redisPort;
	
//	@Value("${ehcache.config-location}")
//	private String ehcachePath;
	
	@Bean
	public RestShiroFilterFactoryBean restShiroFilterFactoryBean(SecurityManager securityManager) {
		RestShiroFilterFactoryBean shiroFilterFactoryBean = new RestShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		
//		shiroFilterFactoryBean.setLoginUrl("/login");
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		
		Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
		filters.put("authc", new RestFormAuthenticationFilter());
		filters.put("perms", new RestAuthorizationFilter());
		filters.put("oauth2Authc", new OAuth2AuthenticationFilter(oAuth2Helper));
		
		Map<String, String> urlPermsMap = shiroService.getUrlPermsMap();
		shiroFilterFactoryBean.setFilterChainDefinitionMap(urlPermsMap);
		return shiroFilterFactoryBean;
	}
	
	/**
	 * 注入 securityManager
	 */
	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setSessionManager(sessionManager());
		securityManager.setRealms(Arrays.asList(userNameRealm(), oAuth2GithubRealm(), oAuth2GiteeRealm()));
		ModularRealmAuthenticator authenticator = new EnhanceModularRealmAuthenticator();
		securityManager.setAuthenticator(authenticator);
		authenticator.setRealms(Arrays.asList(userNameRealm(), oAuth2GithubRealm(), oAuth2GiteeRealm()));
		SecurityUtils.setSecurityManager(securityManager);
		return securityManager;
	}
	
	/**
	 * Github 登录 Realm
	 */
	@Bean
	public OAuth2GithubRealm oAuth2GithubRealm() {
		return new OAuth2GithubRealm();
	}
	
	/**
	 * Gitee 登录 Realm
	 */
	@Bean
	public OAuth2GiteeRealm oAuth2GiteeRealm() {
		return new OAuth2GiteeRealm();
	}
	
	/**
	 * 用户名密码登录 Realm
	 */
	@Bean
	public UserNameRealm userNameRealm() {
		UserNameRealm userNameRealm = new UserNameRealm();
		userNameRealm.setCredentialsMatcher(hashedCredentialsMatcher());
		userNameRealm.setCacheManager(redisCacheManager());
//		userNameRealm.setCacheManager(ehCacheManager());
		return userNameRealm;
	}
	
	/**
	 * 用户名密码登录密码匹配器
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		return new RetryLimitHashedCredentialsMatcher("md5");
	}
	
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}
	
	@Bean
	public RedisCacheManager redisCacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		redisCacheManager.setRedisManager(redisManager());
		redisCacheManager.setExpire(shiroActionProperties.getPermsCacheTimeout() == null ? 3600
				: shiroActionProperties.getPermsCacheTimeout());
		redisCacheManager.setPrincipalIdFieldName("userId");
		return redisCacheManager;
	}
	
	@Bean
	public RedisManager redisManager() {
		RedisManager redisManager = new RedisManager();
		redisManager.setHost(redisHost + ":" + redisPort);
		return redisManager;
	}
	
	@Bean
	public RedisSessionDAO redisSessionDAO() {
		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		redisSessionDAO.setExpire(
				shiroActionProperties.getSessionTimeout() == null ? 1800 : shiroActionProperties.getSessionTimeout());
		redisSessionDAO.setRedisManager(redisManager());
		redisSessionDAO.setSessionInMemoryEnabled(false);
		return redisSessionDAO;
	}
	
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(redisSessionDAO());
		sessionManager.setSessionIdUrlRewritingEnabled(false);
		return sessionManager;
	}
	
//	/**
//	 * ehcache缓存管理器；shiro整合ehcache： 通过安全管理器：securityManager 单例的cache防止热部署重启失败
//	 * @return EhCacheManager
//	 */
//	@Bean
//	public EhCacheManager ehCacheManager() {
//		EhCacheManager ehcache = new EhCacheManager();
//		CacheManager cacheManager = CacheManager.getCacheManager("shiro");
//		if (cacheManager == null) {
//			try {
//				cacheManager = CacheManager.create(ResourceUtils.getInputStreamForPath(ehcachePath));
//			} catch (CacheException | IOException e) {
//				e.printStackTrace();
//			}
//		}
//		ehcache.setCacheManager(cacheManager);
//		return ehcache;
//	}
//	
//	/**
//	 * EnterpriseCacheSessionDAO shiro sessionDao层的实现；
//	 * 提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
//	 */
//	@Bean
//	public EnterpriseCacheSessionDAO enterCacheSessionDAO() {
//		EnterpriseCacheSessionDAO enterCacheSessionDAO = new EnterpriseCacheSessionDAO();
//		// 添加ehcache活跃缓存名称（必须和ehcache缓存名称一致）
//		enterCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
//		return enterCacheSessionDAO;
//	}
//	
//	/**
//	 *
//	 * @描述：sessionManager添加session缓存操作DAO
//	 * @创建人：wyait
//	 * @创建时间：2018年4月24日 下午8:13:52
//	 * @return
//	 */
//	@Bean
//	public DefaultWebSessionManager sessionManager() {
//		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//		sessionManager.setSessionDAO(enterCacheSessionDAO());
//		sessionManager.setSessionIdCookie(sessionIdCookie());
//		return sessionManager;
//	}
//	
//	/**
//	 *
//	 * @描述：自定义cookie中session名称等配置
//	 * @创建人：wyait
//	 * @创建时间：2018年5月8日 下午1:26:23
//	 * @return
//	 */
//	@Bean
//	public SimpleCookie sessionIdCookie() {
//		// DefaultSecurityManager
//		SimpleCookie simpleCookie = new SimpleCookie();
//		// 如果在Cookie中设置了"HttpOnly"属性，那么通过程序(JS脚本、Applet等)将无法读取到Cookie信息，这样能有效的防止XSS攻击。
//		simpleCookie.setHttpOnly(true);
//		simpleCookie.setName("SHRIOSESSIONID");
//		simpleCookie.setMaxAge(86400);// 30分钟
//		return simpleCookie;
//	}
}