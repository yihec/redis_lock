package com.redis.lock.service;

import com.alibaba.druid.util.StringUtils;
//import com.redis.lock.config.RedisDistributedLock;
import com.redis.lock.config.RedissonLock;
import com.redis.lock.dao.UserDao;
import com.redis.lock.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DemoService {
    @Autowired
    private UserDao userDao;
    @Autowired
    RedissonLock redisDistributedLock;

    @Transactional
    public void updateData(){
        User query = userDao.queryDataById(1);
        int age = query.getAge();
        age+=1;
        query.setAge(age);
        userDao.updatetData(query);

    }

    public void updateData2(){
        redisDistributedLock.acquire("lock");
        User query = userDao.queryDataById(2);
        int age = query.getAge();
        age+=1;
        query.setAge(age);
        userDao.updatetData(query);
        redisDistributedLock.release("lock");

    }
}
