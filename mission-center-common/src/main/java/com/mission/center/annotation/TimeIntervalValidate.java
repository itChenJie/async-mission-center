package com.mission.center.annotation;



import com.mission.center.constants.TimeIntervalFieldEnum;
import com.mission.center.constants.TimeUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeIntervalValidate {
    /**
     * 时间 间隔 日期类型
     *
     * @return {@link ChronoUnit}
     */
    TimeUnit intervalType () default TimeUnit.DAY;
    /**
     * 时间间隔
     * @return
     */
    long interval() default 1;
    /**
     * 字段分组
     * @return
     */
    int group() default 0;

    /**
     * 时间区间类型
     * @return {@link TimeIntervalFieldEnum}
     */
    TimeIntervalFieldEnum type();
}
