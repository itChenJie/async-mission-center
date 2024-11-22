package com.mission.center.annotation;

import java.lang.annotation.*;

/**
 * @Author: chenWenJie
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiEnumProperty {
    Class<? extends Enum<?>> value();
}
