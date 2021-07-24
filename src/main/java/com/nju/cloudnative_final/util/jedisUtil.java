package com.nju.cloudnative_final.util;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @program: CloudNative_final
 * @description:
 * @author: Gxy-2001
 * @create: 2021-07-14
 */
public class jedisUtil {
    private static JedisPool pool = null;

    public static JedisPool getJedisPool() {
        if (pool == null) {
            String ip = "127.0.0.1";
            int port = 6379;
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(18);
            jedisPoolConfig.setMaxIdle(8);
            pool = new JedisPool(jedisPoolConfig, ip, port, 10000);
        }
        return pool;
    }
}
