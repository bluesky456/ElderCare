package com.ovvi.api.redis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ovvi.api.redis.PushRedis;
import com.ovvi.api.utils.CloseableUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

@Component("redisImpl")
public class RedisImpl {

	private Logger logger = LoggerFactory.getLogger(RedisImpl.class);

	/**
	 * 
	 * @param redisInstance
	 * @param keyStr
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月13日下午3:25:12
	 */
	public boolean exists(PushRedis instance, String keyStr) {
		boolean res = false;
		if (null == instance) {
			return res;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			res = jedis.exists(keyStr);
			return res;
		} catch (Exception e) {
			logger.error(String.format("redis exists exception key=%s\ne=%s", keyStr, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return res;

	}

	/**
	 * 
	 * @param redisInstance
	 * @param key
	 * @param field
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月13日下午3:25:20
	 */
	public String hget(PushRedis instance, String key, String field) {
		if (null == instance) {
			return "";
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hget(key, field);
		} catch (Exception e) {
			logger.error(String.format("redis hget exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return "";
	}

	/**
	 * 
	 * @param redisInstance
	 * @param key
	 * @param field
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月13日下午3:29:23
	 */
	public Boolean hexists(PushRedis instance, String key, String field) {
		if (null == instance) {
			return false;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hexists(key, field);
		} catch (Exception e) {
			logger.error(String.format("redis hexists exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return false;
	}

	/** 
	 * @param REDIS_TEST
	 * @param json
	 * @author liuyunlong
	 * @version 2017年3月13日下午3:40:07
	 */
	public void rpush(PushRedis instance, String key, String json) {
		if (null == instance) {
			return;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			// 将用户注册信息缓存到队列，将数据添加到队列尾部
			jedis.rpush(key, json);
		} catch (Exception e) {
			logger.error(String.format("redis rpush exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
	}

	/**
	 * 
	 * @param redisInstance
	 * @param key
	 * @param field
	 * @param value
	 * @author liuyunlong
	 * @version 2017年3月13日下午5:50:07
	 * @return 
	 */
	public Long hset(PushRedis instance, String key, String field, String value) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			logger.error(String.format("redis hset exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;

	}

	/**
	 * 
	 * @param key
	 * @param integer
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月13日下午8:36:39
	 */
	public Long incrBy(PushRedis instance, String key, Integer integer) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.incrBy(key, integer);
		} catch (Exception e) {
			logger.error(String.format("redis incrBy exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/** 
	 * @param REDIS_TEST
	 * @param string
	 * @param product
	 * @param i
	 * @author liuyunlong
	 * @version 2017年3月13日下午8:41:01
	 */
	public void hincrBy(PushRedis instance, String key, String field, Integer value) {
		if (null == instance) {
			return;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			logger.error(String.format("redis hincrBy exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
	}

	/**
	 * 
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月15日下午8:17:47
	 */
	public List<String> lrange(PushRedis instance, String key) {
		List<String> result = new ArrayList<String>();
		if (null == instance) {
			return result;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			result = jedis.lrange(key, 0, -1);
			return result;
		} catch (Exception e) {
			logger.error(String.format("redis lrange exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return result;
	}

	/**
	 * 
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月15日下午8:17:50
	 */
	public String get(PushRedis instance, String key) {
		if (null == instance) {
			return "";
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			logger.error(String.format("redis get exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return "";
	}

	/**
	 * 
	 * @param instance
	 * @param key
	 * @param strs
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月15日下午8:17:54
	 */
	public Long lpush(PushRedis instance, String key, String... strs) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.lpush(key, strs);
		} catch (Exception e) {
			logger.error(String.format("redis lpush exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月15日下午8:18:00
	 */
	public Long incr(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.incr(key);
		} catch (Exception e) {
			logger.error(String.format("redis incr exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 设置新的定时增加key
	 * @param instance
	 * @param key
	 * @param timeSec
	 * @throws Exception
	 * @author liuyunlong
	 * @version 2017年3月15日下午9:21:45
	 */
	public void setNewIncrTimeOutKey(PushRedis instance, String key, int timeSec) {
		if (null == instance) {
			return;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			Transaction t = jedis.multi();
			t.incr(key);
			t.expire(key, timeSec);
			t.exec();
		} catch (Exception e) {
			logger.error(String.format("redis setNewIncrTimeOutKey exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
	}

	/**
	 * 检查是否到期并设置新的时间
	 * @param instance
	 * @param key
	 * @param timeSec
	 * @author liuyunlong
	 * @version 2017年3月15日下午9:27:05
	 */
	public void checkAndSetTTL(PushRedis instance, String key, int timeSec) {
		if (null == instance) {
			return;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			long res = jedis.ttl(key);
			if (res < 0) {
				jedis.expire(key, timeSec);
			}
		} catch (Exception e) {
			logger.error(String.format("redis checkAndSetTTL exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}

	}

	public String set(PushRedis instance, String key, String value) {
		if (null == instance) {
			return "";
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.set(key, value);
		} catch (Exception e) {
			logger.error(String.format("redis set exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return "";
	}

	/**
	 * 
	 * @param instance
	 * @param key
	 * @param fields
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月16日下午8:47:50
	 */
	public Long hdel(PushRedis instance, String key, String... fields) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hdel(key, fields);
		} catch (Exception e) {
			logger.error(String.format("redis hdel exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年3月16日下午9:09:40
	 */
	public Long hlen(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hlen(key);
		} catch (Exception e) {
			logger.error(String.format("redis hlen exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 
	 * @param instance
	 * @param pattern
	 * @return
	 * @author liuyunlong
	 * @version 2017年5月10日上午10:58:39
	 */
	public Set<String> keys(PushRedis instance, String pattern) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.keys(pattern);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年6月2日下午3:41:42
	 */
	public Map<String, String> hgetAll(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			logger.error(String.format("redis hgetAll exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/** 
	 * @param instance
	 * @param key
	 * @author liuyunlong
	 * @version 2017年6月5日上午9:22:47
	 * @return 
	 */
	public Set<String> hkeys(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hkeys(key);
		} catch (Exception e) {
			logger.error(String.format("redis hkeys exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/** 
	 * 删除key值
	 * @param rEDIS_OTHER
	 * @param value
	 * @author liuyunlong
	 * @version 2017年10月12日下午5:38:00
	 */
	public void del(PushRedis instance, String key) {
		if (null == instance) {
			return;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			jedis.del(key);
		} catch (Exception e) {
			logger.error(String.format("redis del exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
	}

	/**
	 * set中批量插入 
	 * @param instance
	 * @param key
	 * @param values
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月16日上午11:25:24
	 */
	public Long sadd(PushRedis instance, String key, String[] values) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.sadd(key, values);
		} catch (Exception e) {
			logger.error(String.format("redis sadd exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/** 
	 * set单条插入
	 * @param instance
	 * @param value
	 * @param string
	 * @author liuyunlong
	 * @version 2017年10月16日下午2:04:31
	 * @return 
	 */
	public Long sadd(PushRedis instance, String key, String value) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			logger.error(String.format("redis sadd exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 取set集合中的数量
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月25日上午9:57:09
	 */
	public Long scard(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			logger.error(String.format("redis scard exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 获取set中所有元素
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月16日下午6:34:51
	 */
	public Set<String> smembers(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.smembers(key);
		} catch (Exception e) {
			logger.error(String.format("redis smembers exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 元素是否存在集合中
	 * @param instance
	 * @param key
	 * @param value
	 * @return
	 * @author liuyunlong
	 * @version 2017年12月20日下午9:30:06
	 */
	public boolean sismember(PushRedis instance, String key, String value) {
		if (null == instance) {
			return false;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.sismember(key, value);
		} catch (Exception e) {
			logger.error(String.format("redis sismember exception key=%s;value=%s\ne=%s", key, value, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return false;
	}

	/**
	 * 从set中随机取一个元素并删除
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月16日下午7:12:36
	 */
	public String spop(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.spop(key);
		} catch (Exception e) {
			logger.error(String.format("redis spop exception key=%s\ne=%s", key, e.toString()));
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 返回hash字段的所有值
	 * @param instance
	 * @param key
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月16日下午7:24:31
	 */
	public List<String> hvals(PushRedis instance, String key) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.hvals(key);
		} catch (Exception e) {
			logger.error(String.format("redis hvals exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 从set中删除指定元素
	 * @param instance
	 * @param key
	 * @param value
	 * @return
	 * @author liuyunlong
	 * @version 2017年10月17日上午9:07:05
	 */
	public Long srem(PushRedis instance, String key, String value) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.srem(key, value);
		} catch (Exception e) {
			logger.error(String.format("redis srem exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/** 
	 * 取两个set的差集
	 * @param instance
	 * @param value
	 * @param value2
	 * @author liuyunlong
	 * @version 2017年10月19日下午2:51:39
	 */
	public Set<String> sdiff(PushRedis instance, String key1, String key2) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.sdiff(key1, key2);
		} catch (Exception e) {
			logger.error(String.format("redis sdiff exception key=%s\ne=%s", key1, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/** 
	 * @param 批量删除key
	 * @param array
	 * @author liuyunlong
	 * @version 2017年10月20日下午5:32:35
	 */
	public Long del(PushRedis instance, String[] keys) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.del(keys);
		} catch (Exception e) {
			logger.error(String.format("redis del exception key=%s\ne=%s", keys, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}

	/**
	 * 设置key的有效期
	 * @param instance
	 * @param key
	 * @param unixTime
	 * @return
	 * @author liuyunlong
	 * @version 2018年1月9日上午9:44:05
	 */
	public Long expireAt(PushRedis instance, String key, long unixTime) {
		if (null == instance) {
			return null;
		}
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = instance.getJedisPool();
			jedis = pool.getResource();
			return jedis.expireAt(key, unixTime);
		} catch (Exception e) {
			logger.error(String.format("redis expireAt exception key=%s\ne=%s", key, e.toString()));
			e.printStackTrace();
		} finally {
			CloseableUtil.close(jedis);
		}
		return null;
	}
}
