package com.ovvi.api.interceptor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.ovvi.api.redis.PushRedis;
import com.ovvi.api.utils.PropertiesUtil;

public class AuthIntecepter implements ServletContextListener {

	private static Logger log = Logger.getLogger(AuthIntecepter.class);
	private static final String DEFAULT_IP = "192.168.9.91";
	private static final String DEFAULT_PORT = "7003";

	/**************************REDIS IP & PORT**************************/
	public static final String REDIS_IP_ELDER = PropertiesUtil.getValue("redis_ip_elder", DEFAULT_IP);
	public static final Integer REDIS_PORT_ELDER = Integer.parseInt(PropertiesUtil.getValue("redis_port_elder", DEFAULT_PORT));

	/****************************REDIS 配置******************************/
	public static final Integer REDIS_MAX_ACTIVES = Integer.parseInt(PropertiesUtil.getValue("redis_max_actives", "80"));
	public static final Integer REDIS_MAX_WAIT = Integer.parseInt(PropertiesUtil.getValue("redis_max_wait", "1000"));
	public static final Integer REDIS_TIME_OUT = Integer.parseInt(PropertiesUtil.getValue("redis_time_out", "5000"));
	public static final Integer REDIS_MAX_IDLE = Integer.parseInt(PropertiesUtil.getValue("redis_max_idle", "40"));
	public static final int REDIS_MIN_IDLE = Integer.parseInt(PropertiesUtil.getValue("redis_min_idle", "5"));
	public static final boolean REDIS_TEST_ON_BORROW = Boolean.parseBoolean(PropertiesUtil.getValue("redis.pool.testOnBorrow", "true"));
	public static final boolean REDIS_TEST_ON_RETURN = Boolean.parseBoolean(PropertiesUtil.getValue("redis.pool.testOnReturn", "true"));
	public static final boolean REDIS_TEST_WHILE_IDLE = Boolean.parseBoolean(PropertiesUtil.getValue("redis.pool.testWhileIdle", "true"));
	public static final long REDIS_BETWEEN_EVICTION_RUNS_MILLIS = Long.parseLong(PropertiesUtil.getValue("redis.pool.timeBetweenEvictionRuns", "60000"));
	public static final long REDIS_MIN_EVICTABLE_IDLE_TIME_MILLIS = Long.parseLong(PropertiesUtil.getValue("redis.pool.minEvictableIdleTime", "30000"));
	public static final Integer REDIS_NUM_TESTS_PER_EVICTION_RUN = Integer.parseInt(PropertiesUtil.getValue("redis.pool.numTestsPerEvictionRun", "-1"));

	/****************************REDIS 实例******************************/
	public static PushRedis REDIS_ELDER;

	public void contextDestroyed(ServletContextEvent sce) {

	}

	public void contextInitialized(ServletContextEvent sce) {
		log.debug("初始化信息");
		REDIS_ELDER = new PushRedis(AuthIntecepter.REDIS_IP_ELDER, AuthIntecepter.REDIS_PORT_ELDER);
	}

}
