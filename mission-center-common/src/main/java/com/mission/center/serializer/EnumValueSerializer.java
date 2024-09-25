package com.mission.center.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 枚举类型字段序列化 枚举value 序列化
 */
public class EnumValueSerializer implements ObjectSerializer {
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object instanceof Enum) {
//            IEnum anEnum = (IEnum) object;
//            Object value = anEnum.getValue();
//            if (value instanceof String){
//                out.writeString((String) value);
//                return;
//            }
//            if (value instanceof Integer){
//                out.writeInt((Integer) value);
//                return;
//            }
//            out.write(String.valueOf(value));
            Enum<?> anEnum = (Enum<?>) object;
            out.writeString(anEnum.name());
        }
    }

}
