package com.mission.center.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum TimeUnit  {
    DAY("天","DAY"),
    WEEKS("周","WEEKS"),
    MONTH("月","MONTH"),
    YEARS("年","YEARS");
    /**
     * 描述
     */
    private String name;
    /**
     * 编码
     */
    private String value;

    public static TimeUnit of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.value.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exists"));
    }


}
