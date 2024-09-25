package com.mission.center.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @Description 业务模块枚举
 */
@Getter
@AllArgsConstructor
public enum ServiceModelEnum implements IEnum<String> {
    APP("出单","APP"),
    ERP("ERP","ERP");
    /**
     * 描述
     */
    private String name;
    /**
     * 编码
     */
    private String value;

    @Override
    public String getValue() {
        return value;
    }

    public static ServiceModelEnum of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.value.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }

    public static String getValueBykey(String key) {
        ServiceModelEnum[] applicationStateEnums = values();
        for (ServiceModelEnum itemEnum : applicationStateEnums) {
            if (itemEnum.name.equals(key)) {
                return itemEnum.getValue();
            }
        }
        throw new IllegalArgumentException(key + " not exists");
    }


    public static String getKeyByValue(String value) {
        for (ServiceModelEnum itemEnum : ServiceModelEnum.values()) {
            if (value.equals(itemEnum.getValue())) {
                return itemEnum.getName();
            }
        }
        throw new IllegalArgumentException(value + " not exists");
    }

    @Override
    public String toString() {
        return "ServiceModelEnum{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
