package com.redis.lock.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class RedissonManager {

//    @Value("${spring.profiles.active}")
//    public String env;

    private Config config = new Config();

    private RedissonClient redisson = null;

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedissonClient init() {
        try {
//            if ("prod".equals(env)) {
//                List<String> nodes = redisProperties.getCluster().getNodes();
//                List<String> nodes2 = new ArrayList<>();
//                for (String s:nodes){
//                    nodes2.add("redis://"+s);
//                }
//                //生产环境redis集群配置
//                config.useClusterServers()
//                        .setPassword(redisProperties.getPassword())
//                        .addNodeAddress(nodes2.get(0), nodes2.get(1), nodes2.get(2),
//                                nodes2.get(3),nodes2.get(4),nodes2.get(5));
//
//
//            } else {
//
//            }

            //单机配置
            String address = redisProperties.getHost() + ":" + redisProperties.getPort();
            config
                    .useSingleServer()
                    .setAddress("redis://" + address)
                    .setPassword(redisProperties.getPassword())
                    .setDatabase(0);
            redisson = Redisson.create(config);

        } catch (Exception e) {
            log.error("初始化Redisson 异常 error ={}", e);
        }
        return redisson;
    }

}