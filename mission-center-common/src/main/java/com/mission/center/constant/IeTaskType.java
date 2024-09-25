package com.mission.center.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;


@Getter
@AllArgsConstructor
public enum IeTaskType implements IEnum<Integer> {
    EXPORT(0,"导出"),
    IMPORT(1,"导入"),
    CLEANSE(2,"数据处理");
    @EnumValue
    private Integer value;

    private String name;

    @Override
    public Integer getValue() {
        return value;
    }

    public static IeTaskType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.value.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }

    public static Integer getValueBykey(String key) {
        IeTaskType[] applicationStateEnums = values();
        for (IeTaskType itemEnum : applicationStateEnums) {
            if (itemEnum.name.equals(key)) {
                return itemEnum.getValue();
            }
        }
        throw new IllegalArgumentException(key + " not exists");
    }


    public static String getKeyByValue(Integer value) {
        for (IeTaskType itemEnum : IeTaskType.values()) {
            if (value.equals(itemEnum.getValue())) {
                return itemEnum.getName();
            }
        }
        throw new IllegalArgumentException(value + " not exists");
    }

    @Override
    public String toString() {
        return "IeTaskType{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
