package com.mission.center.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Description 导出模版枚举
 */
@Getter
@AllArgsConstructor
public enum ExportTemplateType implements IEnum<String> {
    DYNAMIC("动态","dynamic"),
    CUSTOM("自定义","custom");

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

    public static ExportTemplateType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.value.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }

    public static String getValueBykey(String key) {
        ExportTemplateType[] applicationStateEnums = values();
        for (ExportTemplateType itemEnum : applicationStateEnums) {
            if (itemEnum.name.equals(key)) {
                return itemEnum.getValue();
            }
        }
        throw new IllegalArgumentException(key + " not exists");
    }


    public static String getKeyByValue(Integer value) {
        for (ExportTemplateType itemEnum : ExportTemplateType.values()) {
            if (value.equals(itemEnum.getValue())) {
                return itemEnum.getName();
            }
        }
        throw new IllegalArgumentException(value + " not exists");
    }

    @Override
    public String toString() {
        return "ExportTemplateType{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
