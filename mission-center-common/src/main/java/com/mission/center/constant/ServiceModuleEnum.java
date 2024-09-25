package com.mission.center.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Description 业务模块枚举
 */
@Getter
@AllArgsConstructor
public enum ServiceModuleEnum implements IEnum<String> {
    GLHT("管理后台","GLHT"),
    ERP("ERP","ERP");
    /**
     * 描述
     */
    private String name;
    /**
     * 编码
     */
    @EnumValue
    private String value;

    @Override
    public String getValue() {
        return value;
    }

    public static ServiceModuleEnum of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.value.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }

    public static String getValueBykey(String key) {
        ServiceModuleEnum[] applicationStateEnums = values();
        for (ServiceModuleEnum itemEnum : applicationStateEnums) {
            if (itemEnum.name.equals(key)) {
                return itemEnum.getValue();
            }
        }
        throw new IllegalArgumentException(key + " not exists");
    }


    public static String getKeyByValue(String value) {
        for (ServiceModuleEnum itemEnum : ServiceModuleEnum.values()) {
            if (value.equals(itemEnum.getValue())) {
                return itemEnum.getName();
            }
        }
        throw new IllegalArgumentException(value + " not exists");
    }

    @Override
    public String toString() {
        return "ServiceModuleEnum{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
