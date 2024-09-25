package com.mission.center.server.util;

import com.mission.center.constants.RedisKey;
import com.mission.center.util.CodeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.SetOperations;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class CodeUtilBean {

    @Autowired
    protected StringRedisTemplate redisTemplate;

    public String getIeTaskCode(int type) {
        String code = CodeUtil.getCode(type);
        SetOperations<String, String> opsForSet = redisTemplate.opsForSet();
        String key = RedisKey.TASK_CODE_GEN+code;
        Set<String> codes = opsForSet.members(key);
        if(codes!=null && codes.contains(code)) {
            return getIeTaskCode(type);
        }
        opsForSet.add(key, code);
        if (CollectionUtils.isEmpty(codes)) {//第一次需要设置有效期
            redisTemplate.expire(key, 3, TimeUnit.MINUTES);
        }
        return code;
    }
}
