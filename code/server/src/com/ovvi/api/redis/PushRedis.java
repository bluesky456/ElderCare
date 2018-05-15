package com.ovvi.api.redis;

import com.ovvi.api.interceptor.AuthIntecepter;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class PushRedis {
	private JedisPool jedisPool;

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public PushRedis(String strIP, int nPort) {
		initialPool(strIP, nPort);
	}

	private void initialPool(String strIP, int nPort) {
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxTotal(AuthIntecepter.REDIS_MAX_ACTIVES);
		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(AuthIntecepter.REDIS_MAX_IDLE);
		config.setMinIdle(AuthIntecepter.REDIS_MIN_IDLE);
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWaitMillis(AuthIntecepter.REDIS_MAX_WAIT);
		// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(AuthIntecepter.REDIS_TEST_ON_BORROW);
		config.setTestOnReturn(AuthIntecepter.REDIS_TEST_ON_RETURN);
		config.setTestWhileIdle(AuthIntecepter.REDIS_TEST_WHILE_IDLE);
		// 表示idle object evitor两次扫描之间要sleep的毫秒数；
		config.setTimeBetweenEvictionRunsMillis(AuthIntecepter.REDIS_BETWEEN_EVICTION_RUNS_MILLIS);
		config.setMinEvictableIdleTimeMillis(AuthIntecepter.REDIS_MIN_EVICTABLE_IDLE_TIME_MILLIS);
		config.setNumTestsPerEvictionRun(AuthIntecepter.REDIS_NUM_TESTS_PER_EVICTION_RUN);
		this.jedisPool = new JedisPool(config, strIP, nPort, AuthIntecepter.REDIS_TIME_OUT);
	}
}
