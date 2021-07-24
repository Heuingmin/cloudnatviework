package com.nju.cloudnative_final.limit;

import com.nju.cloudnative_final.util.IpUtil;
import com.nju.cloudnative_final.util.jedisUtil;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @program: CloudNative_final
 * @description:
 * @author: Gxy-2001
 * @create: 2021-07-14
 */
@Component
@Scope
@Aspect
@Slf4j
public class LimitAspect {
    @Pointcut("@annotation(com.nju.cloudnative_final.limit.Limit)")
    public void Limit() {
    }

    //@Around("Limit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        Object obj = null;
        //方法
        MethodSignature methodSig = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getMethod(methodSig.getName(), methodSig.getParameterTypes());
        //获取注解
        Limit annotation = method.getAnnotation(Limit.class);

        if (annotation != null) {
            String functionName = method.getName();
            String IP = IpUtil.getIpAddr();
            //生成不同的桶名称
            String BUCKET = "LIMIT_" + IP + "_" + functionName;
            String BUCKET_MONITOR = "LIMIT_MONITOR_" + IP + "_" + functionName;
            long now = System.currentTimeMillis();

            //获取 Jedis
            Jedis jedis = jedisUtil.getJedisPool().getResource();

            //删除之前的请求，计数器自增
            Transaction transaction = jedis.multi();
            //删除之前的
            transaction.zremrangeByScore(BUCKET_MONITOR.getBytes(), "-inf".getBytes(), String.valueOf(now - annotation.time()).getBytes());
            ZParams params = new ZParams();
            params.weights(1.0, 0.0);
            transaction.zinterstore(BUCKET, params, BUCKET, BUCKET_MONITOR);
            List<Object> results = transaction.exec();
            long counter = (Long) results.get(results.size() - 1);

            //获取一个唯一的ID
            String ID = UUID.randomUUID().toString();
            //加入当前的请求
            transaction = jedis.multi();
            transaction.zadd(BUCKET, counter, ID);
            transaction.zadd(BUCKET_MONITOR, now, ID);
            transaction.zrank(BUCKET, ID);
            results = transaction.exec();

            long rank = (Long) results.get(results.size() - 1);
            if (rank < annotation.limitNum() - 1) {
                try {
                    log.info("顺利执行");
                    //proceed方法就是用于启动方法
                    obj = point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                log.info("限流！流量过大");
                transaction = jedis.multi();
                transaction.zrem(BUCKET_MONITOR, ID);
                transaction.zrem(BUCKET, ID);
                transaction.exec();
                jedis.close();
                throw new LimitException();
            }
            jedis.close();
        }

        return obj;
    }


    private Map<String, Pair<Integer, Long>> map = new HashMap<>();

    @Around("Limit()")
    public Object pointcutWithoutRedis(ProceedingJoinPoint point) throws Throwable {
        //方法
        MethodSignature methodSig = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getMethod(methodSig.getName(), methodSig.getParameterTypes());
        //获取注解
        Limit annotation = method.getAnnotation(Limit.class);

        if (annotation != null) {
            String functionName = method.getName();
            String IP = IpUtil.getIpAddr();
            String key = "LIMIT_".concat(IP).concat(functionName);
            if (!map.containsKey(key)) {
                //接口未访问过
                map.put(key, new Pair<>(1, System.currentTimeMillis()));
            } else {
                int count = map.get(key).getKey();
                long time = map.get(key).getValue();
                long now = System.currentTimeMillis();
                if (now - time >= annotation.time()) {
                    map.put(key, new Pair<>(1, now));
                } else if (count > annotation.limitNum() - 1) {
                    throw new LimitException();
                } else {
                    map.put(key, new Pair<>(map.get(key).getKey() + 1, time));
                }
            }
        }
        return point.proceed();
    }
}
