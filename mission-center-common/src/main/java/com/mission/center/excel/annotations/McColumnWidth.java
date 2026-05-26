package com.mission.center.excel.annotations;

import java.lang.annotation.*;

/**
 * 列宽注解
 * 用于设置 Excel 导出列的宽度
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface McColumnWidth {
    /**
     * 列宽（默认 4096）
     */
    int value() default 4096;
}