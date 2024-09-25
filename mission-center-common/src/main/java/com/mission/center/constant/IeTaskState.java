package com.mission.center.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Description 导入导出任务状态 枚举
 */
@Getter
@AllArgsConstructor
public enum IeTaskState implements IEnum<Integer> {
    WAIT_START(0, "待启动"),
    EXECUTING(1, "执行中"),
    FILE_UPLOADING(2, "文件待上传"),
    FAIL(3, "失败"),
    SUCCESS(4, "成功");
    @EnumValue
    private Integer value;

    private String name;

    @Override
    public Integer getValue() {
        return this.value;
    }

    public static IeTaskState of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.value.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }

    public static Integer getValueBykey(String key) {
        IeTaskState[] applicationStateEnums = values();
        for (IeTaskState itemEnum : applicationStateEnums) {
            if (itemEnum.name.equals(key)) {
                return itemEnum.getValue();
            }
        }
        throw new IllegalArgumentException(key + " not exists");
    }


    public static String getKeyByValue(Integer value) {
        for (IeTaskState itemEnum : IeTaskState.values()) {
            if (value.equals(itemEnum.getValue())) {
                return itemEnum.getName();
            }
        }
        throw new IllegalArgumentException(value + " not exists");
    }

    @Override
    public String toString() {
        return "IeTaskState{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
