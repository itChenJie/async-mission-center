package com.mission.center.excel.annotations;

import javax.validation.groups.Default;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McExcelProperty {
    /**
     * 对应的字段名(表头名)
     *
     * @return {@link String}
     */
    String value() default "";

    /**
     * 顺序值
     * excel上的列的顺序（默认为类中属性的顺序）
     */
    int order() default Integer.MAX_VALUE;

    /**
     * 列宽
     */
    int width() default 2048*2;

    /**
     * 日期格式
     * 针对日期属性序列化的格式 例如：yyyy-MM-dd
     * 默认是yyyy-MM-dd HH:mm:ss
     */
    String dateFormatter() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认分组
     */
    Class<?>[] group() default Default.class;
}