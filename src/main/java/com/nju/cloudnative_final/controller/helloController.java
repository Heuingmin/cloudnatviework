package com.nju.cloudnative_final.controller;


import com.nju.cloudnative_final.limit.Limit;
import com.nju.cloudnative_final.service.helloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: CloudNative_final
 * @description:
 * @author: Gxy-2001
 * @create: 2021-07-14
 */
@Slf4j
@RestController
public class helloController {


    @Autowired
    private helloService helloService;

    @Limit(limitNum = 100)
    @GetMapping("/hello")
    public Object greeting() {
        return  helloService.Hello();
    }
}
