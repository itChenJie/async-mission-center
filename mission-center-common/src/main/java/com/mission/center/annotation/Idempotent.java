package com.mission.center.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /**
     * Redis Key 前缀
     */
    String prefix() default "idempotent:";

    /**
     * 锁的过期时间（秒），默认 30 秒
     */
    long expire() default 30;
    
    /**
     * 提示信息
     */
    String message() default "操作过于频繁，请稍后！";
}