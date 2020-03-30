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
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.maint.common.shiro.ModelRealmAuthenticator;
import com.maint.common.shiro.OAuth2Helper;
import com.maint.common.shiro.RestShiroFilterFactoryBean;
import com.maint.common.shiro.ShiroActionProperties;
import com.maint.common.shiro.credential.RetryLimitHashedCredentialsMatcher;
import com.maint.common.shiro.filter.KickoutSessionFilter;
import com.maint.common.shiro.filter.MobileFormAuthenticationFilter;
import com.maint.common.shiro.filter.OAuth2AuthenticationFilter;
import com.maint.common.shiro.filter.RestAuthorizationFilter;
import com.maint.common.shiro.filter.RestFormAuthenticationFilter;
import com.maint.common.shiro.filter.WebFormAuthenticationFilter;
import com.maint.common.shiro.realm.OAuth2GiteeRealm;
import com.maint.common.shiro.realm.OAuth2GithubRealm;
import com.maint.common.shiro.realm.ManageRealm;
import com.maint.common.shiro.realm.MobileRealm;
import com.maint.common.shiro.realm.WebRealm;
import com.maint.system.service.ShiroService;

import javax.annotation.Resource;
import javax.servlet.Filter;

import java.io.IOException;
import java.util.Arrays;
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
	
//	@Value("${spring.redis.host}")
//	private String redisHost;
//	
//	@Value("${spring.redis.port}")
//	private Integer redisPort;
	
	@Value("${LoginURL}")
	private String URMPUrl;
	
	@Value("${ehcache.config-location}")
	private String ehcachePath;
	
	@Bean
	public RestShiroFilterFactoryBean restShiroFilterFactoryBean(SecurityManager securityManager) {
		RestShiroFilterFactoryBean shiroFilterFactoryBean = new RestShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
//		shiroFilterFactoryBean.setLoginUrl("/login");
		Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
		filters.put("kickout", kickoutSessionFilter());
		filters.put("authc", new RestFormAuthenticationFilter());
		filters.put("webAuthc", webAuthenticationFiler());
		filters.put("mobileAuthc", mobileAuthenticationFiler());
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
		securityManager.setRealms(Arrays.asList(userNameRealm(), webUserRealm(), mobileRealm(), oAuth2GithubRealm(), oAuth2GiteeRealm()));
		ModularRealmAuthenticator authenticator = new ModelRealmAuthenticator();
		securityManager.setAuthenticator(authenticator);
		authenticator.setRealms(Arrays.asList(userNameRealm(), webUserRealm(), mobileRealm(), oAuth2GithubRealm(), oAuth2GiteeRealm()));
		SecurityUtils.setSecurityManager(securityManager);
		return securityManager;
	}
	
	public WebFormAuthenticationFilter webAuthenticationFiler() {
		WebFormAuthenticationFilter webFormAuthenticationFilter = new WebFormAuthenticationFilter();
		webFormAuthenticationFilter.setLoginUrl("/website/view/toLogin");
		webFormAuthenticationFilter.setUsernameParam("username");
		webFormAuthenticationFilter.setPasswordParam("password");
		return webFormAuthenticationFilter;
	}
	
	public MobileFormAuthenticationFilter mobileAuthenticationFiler() {
		MobileFormAuthenticationFilter mobileFormAuthenticationFilter = new MobileFormAuthenticationFilter();
		mobileFormAuthenticationFilter.setLoginUrl("/mobile/toLogin");
		mobileFormAuthenticationFilter.setUsernameParam("username");
		mobileFormAuthenticationFilter.setPasswordParam("password");
		return mobileFormAuthenticationFilter;
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
	public ManageRealm userNameRealm() {
		ManageRealm userNameRealm = new ManageRealm();
		userNameRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//		userNameRealm.setCacheManager(redisCacheManager());
		userNameRealm.setCacheManager(ehCacheManager());
		return userNameRealm;
	}
	
	/**
	 * WebRealm
	 */
	@Bean
	public WebRealm webUserRealm() {
		WebRealm webRealm = new WebRealm();
		webRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//		webRealm.setCacheManager(redisCacheManager());
		webRealm.setCacheManager(ehCacheManager());
		return webRealm;
	}
	
	/**
	 * MobileRealm
	 */
	@Bean
	public MobileRealm mobileRealm() {
		MobileRealm mobileRealm = new MobileRealm();
		mobileRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//		mobileRealm.setCacheManager(redisCacheManager());
		mobileRealm.setCacheManager(ehCacheManager());
		return mobileRealm;
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
	
	
	
	
	
	
	
	
	
	
//	@Bean
//	public RedisCacheManager redisCacheManager() {
//		RedisCacheManager redisCacheManager = new RedisCacheManager();
//		redisCacheManager.setRedisManager(redisManager());
//		redisCacheManager.setExpire(shiroActionProperties.getPermsCacheTimeout() == null ? 3600
//				: shiroActionProperties.getPermsCacheTimeout());
//		redisCacheManager.setPrincipalIdFieldName("username");
//		return redisCacheManager;
//	}
//	
//	@Bean
//	public RedisManager redisManager() {
//		RedisManager redisManager = new RedisManager();
//		redisManager.setHost(redisHost + ":" + redisPort);
//		return redisManager;
//	}
//	
//	@Bean
//	public RedisSessionDAO redisSessionDAO() {
//		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
//		redisSessionDAO.setExpire(
//				shiroActionProperties.getSessionTimeout() == null ? 1800 : shiroActionProperties.getSessionTimeout());
//		redisSessionDAO.setRedisManager(redisManager());
//		redisSessionDAO.setSessionInMemoryEnabled(false);
//		return redisSessionDAO;
//	}
//	
//	@Bean
//	public DefaultWebSessionManager sessionManager() {
//		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//		sessionManager.setSessionDAO(redisSessionDAO());
//		sessionManager.setSessionIdUrlRewritingEnabled(false);
//		return sessionManager;
//	}
	
	
	
	
	
	
	
	
	
	
	/**
	 *
	 * @描述：kickoutSessionFilter同一个用户多设备登录限制
	 * @创建人：wyait
	 * @创建时间：2018年4月24日 下午8:14:28
	 * @return
	 */
	public KickoutSessionFilter kickoutSessionFilter() {
		KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
		// 使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
		// 这里我们还是用之前shiro使用的ehcache实现的cacheManager()缓存管理
		// 也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
//		kickoutSessionFilter.setCacheManager(redisCacheManager());
		kickoutSessionFilter.setCacheManager(ehCacheManager());
		// 用于根据会话ID，获取会话进行踢出操作的；
		kickoutSessionFilter.setSessionManager(sessionManager());
		// 是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
		kickoutSessionFilter.setKickoutAfter(false);
		// 同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
		kickoutSessionFilter.setMaxSession(1);
		// 被踢出后重定向到的地址；
		kickoutSessionFilter.setKickoutUrl(URMPUrl);
		return kickoutSessionFilter;
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * ehcache缓存管理器；shiro整合ehcache： 通过安全管理器：securityManager 单例的cache防止热部署重启失败
	 * @return EhCacheManager
	 */
	@Bean
	public EhCacheManager ehCacheManager() {
		EhCacheManager ehcache = new EhCacheManager();
		CacheManager cacheManager = CacheManager.getCacheManager("shiro");
		if (cacheManager == null) {
			try {
				cacheManager = CacheManager.create(ResourceUtils.getInputStreamForPath(ehcachePath));
			} catch (CacheException | IOException e) {
				e.printStackTrace();
			}
		}
		ehcache.setCacheManager(cacheManager);
		return ehcache;
	}
	
	/**
	 * EnterpriseCacheSessionDAO shiro sessionDao层的实现；
	 * 提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
	 */
	@Bean
	public EnterpriseCacheSessionDAO enterCacheSessionDAO() {
		EnterpriseCacheSessionDAO enterCacheSessionDAO = new EnterpriseCacheSessionDAO();
		// 添加ehcache活跃缓存名称（必须和ehcache缓存名称一致）
		enterCacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
		return enterCacheSessionDAO;
	}
	
	/**
	 *
	 * @描述：sessionManager添加session缓存操作DAO
	 * @创建人：wyait
	 * @创建时间：2018年4月24日 下午8:13:52
	 * @return
	 */
	@Bean
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionDAO(enterCacheSessionDAO());
		sessionManager.setSessionIdCookie(sessionIdCookie());
		return sessionManager;
	}
	
	/**
	 *
	 * @描述：自定义cookie中session名称等配置
	 * @创建人：wyait
	 * @创建时间：2018年5月8日 下午1:26:23
	 * @return
	 */
	@Bean
	public SimpleCookie sessionIdCookie() {
		// DefaultSecurityManager
		SimpleCookie simpleCookie = new SimpleCookie();
		// 如果在Cookie中设置了"HttpOnly"属性，那么通过程序(JS脚本、Applet等)将无法读取到Cookie信息，这样能有效的防止XSS攻击。
		simpleCookie.setHttpOnly(true);
		simpleCookie.setName("SHRIOSESSIONID");
		simpleCookie.setMaxAge(86400);// 30分钟
		return simpleCookie;
	}
}