package com.nju.cloudnative_final.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: CloudNative_final
 * @description:
 * @author: Gxy-2001
 * @create: 2021-07-14
 */
@Service
public class helloService {
    public Object Hello() {
        Map<String, String> res = new HashMap<>();
        res.put("time", new Date().toString());
        res.put("msg", "Hello");
        return res;
    }
}
