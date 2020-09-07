package com.redis.lock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;

/**
 * redis分布式锁实现
 */
@Slf4j
@Configuration
public class RedisDistributedLock {
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;

    //只在键不存在时，才对键进行设置操作
    private static final String SET_IF_NOT_EXIST = "NX";
    // 设置键的过期时间为 毫秒
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    /**
     * 分布式锁的键值
     */
    private static final  String lockKey = "redis-lock_";

    /**
     * 锁的超时时间 10s
     */
    private static final  int expireTime = 10000;

    /**
     * 锁等待
     */
    private static final int acquireTimeout  = 1 * 5000;

    @Autowired
    private RedisTemplate redisTemplate;

    public String acquire(String key) {
        RedisConnection conn =  RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
        Jedis jedis = null;
        SetParams setParams = new SetParams(); // 相当于设置一个规范，可应用到其他键值对
        setParams.px(expireTime); // 过期时间100s
        setParams.nx();
        try {
             jedis = (Jedis) conn.getNativeConnection();

            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + 50000;
            // 随机生成一个 value
            String requireToken = UUID.randomUUID().toString();
            while (System.currentTimeMillis() < end) {
                String result = jedis.set(lockKey+key, requireToken, setParams);
                if (LOCK_SUCCESS.equals(result)) {
                    log.info("设置redis 锁成功 key = {} value ={}",lockKey+key,requireToken);
                    return requireToken;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("线程异常 ex = {}",e);
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            log.error("获取锁异常 acquire lock due to error", e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }

        return null;
    }

    public boolean release(String identify,String key) {
        Jedis jedis = null;
        RedisConnection conn =  RedisConnectionUtils.getConnection(redisTemplate.getConnectionFactory());
        if (identify == null) {
            return false;
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = new Object();
        try {
            jedis = (Jedis) conn.getNativeConnection();
            result = jedis.eval(script, Collections.singletonList(lockKey+key),
                    Collections.singletonList(identify));
            if (RELEASE_SUCCESS.equals(result)) {
                log.info("释放锁 release lock success, key = {},value:{}",lockKey+key, identify);
                return true;
            }
        } catch (Exception e) {
            log.error("释放锁异常 release lock due to error", e);
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }

        log.info("release lock failed, requestToken:{}, result:{}", identify, result);
        return false;
    }
}
