package com.mission.center.serializer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mission.center.error.ServiceException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 枚举类通用反序列化器
 */
public class CommonEnumDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object o) {
        try {
            if (type instanceof Class) {
                Class<?> clazz = (Class<?>) type;
                if (clazz.isEnum()) {
                    String value = parser.parseObject(String.class);
                    if (StringUtils.isBlank(value))
                        return null;

                    return (T) Enum.valueOf((Class<Enum>) clazz, value);
                }
            }else if (type instanceof ParameterizedType){
                // 集合类型处理
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (List.class.isAssignableFrom((Class<?>) rawType)){
                    List<Enum<?>> list = new ArrayList<>();
                    List<String> values = parser.parseObject(List.class);
                    if (ObjectUtil.isNull(values))
                        return null;

                    for (String value : values) {
                        list.add(Enum.valueOf((Class<Enum>) parameterizedType.getActualTypeArguments()[0], value));
                    }
                    return (T)list;
                }
            }
        }catch (IllegalArgumentException e){
            throw new ServiceException(o+" 传参不符合规范！");
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
