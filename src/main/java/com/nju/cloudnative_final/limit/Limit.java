package com.nju.cloudnative_final.limit;

import java.lang.annotation.*;

/**
 * @program: CloudNative_final
 * @description:
 * @author: Gxy-2001
 * @create: 2021-07-14
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    /**
     * 允许访问的次数
     *
     * @return
     */
    int limitNum() default 100;

    /**
     * 时间段，单位为毫秒，默认值1s
     *
     * @return
     */
    long time() default 1000;
}
