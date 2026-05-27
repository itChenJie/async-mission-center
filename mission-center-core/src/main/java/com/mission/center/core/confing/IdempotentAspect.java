package com.mission.center.core.confing;

import com.alibaba.fastjson.JSON;
import com.mission.center.annotation.Idempotent;
import com.mission.center.error.ServiceException;
import com.mission.center.util.IdempotencyHandlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return joinPoint.proceed();
        }

        // 取第一个参数（通常是 Request DTO）生成 MD5 签名
        String params = JSON.toJSONString(args[0]);
        String requestId = IdempotencyHandlerUtil.generateRequestId(params);
        String lockKey = idempotent.prefix() + requestId;

        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", idempotent.expire(), TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(success)) {
            log.warn("幂等性拦截，触发防重: {}", lockKey);
            throw new ServiceException(idempotent.message());
        }

        return joinPoint.proceed();
    }
}