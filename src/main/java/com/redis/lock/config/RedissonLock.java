package com.redis.lock.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class RedissonLock {

    @Autowired
    private  RedissonClient redissonClient;


    public void acquire(String lockName) {

        String key = lockName;
        RLock myLock = redissonClient.getLock(key);
        //lock提供带timeout参数，timeout结束强制解锁，防止死锁
        myLock.lock(10, TimeUnit.SECONDS);
        log.info("======lock======key"+lockName);
        log.info("======lock======" + Thread.currentThread().getName());
    }

    public void release(String lockName) {
        String key = lockName;
        RLock myLock = redissonClient.getLock(key);
        myLock.unlock();
        log.info("======unlock======" + lockName);
        log.info("======unlock======" + Thread.currentThread().getName());
    }
}
