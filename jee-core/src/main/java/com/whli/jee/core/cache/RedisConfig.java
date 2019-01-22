package com.whli.jee.core.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Desc Redis配置类
 * @Author whli
 * @Version 1.0
 * @Date 2018/5/29 16:15
 */
@Component
public class RedisConfig {
    public static String host;
    public static int port;
    public static int timeout;
    public static int total;
    public static int maxIdle;
    public static long maxWaitMillis;
    public static String password;
    public static int defaultExpire;

    @Value("#{redis['redis.host']}")
    private  String hostRedis;
    @Value("#{redis['redis.port']}")
    private  int portRedis;
    @Value("#{redis['redis.password']}")
    private  String passwordRedis;
    @Value("#{redis['redis.timeout']}")
    private  int timeoutRedis;
    @Value("#{redis['redis.defaultExpire']}")
    private int defaultExpireRedis;

    @Value("#{redis['redis.pool.maxActive']}")
    private  int totalRedis;
    @Value("#{redis['redis.pool.maxIdle']}")
    private  int maxIdleRedis;
    @Value("#{redis['redis.pool.maxWait']}")
    private  long maxWaitMillisRedis;


    /*
     * @Desc Spring容器加载时调用
     * @Author whli
     * @Version 1.0
     * @Date 2018/5/29 16:16
     * @Params []
     * @Return void
     */
    @PostConstruct
    public void init() {
        host = hostRedis;
        port = portRedis;
        timeout = timeoutRedis;
        total = totalRedis;
        maxIdle = maxIdleRedis;
        maxWaitMillis = maxWaitMillisRedis;
        password = passwordRedis;
        defaultExpire = defaultExpireRedis;
    }
}
