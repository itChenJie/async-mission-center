package com.mission.center.core.utils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

import java.util.List;

/**
 *@Description
 * 生成动态报表导出标头class
 **/
public class GenerateDynamicsReportFieldsClassUtils {

    public static Class generateClass(List<Object> titles){
        DynamicType.Builder<?> builder = new ByteBuddy()
                .subclass(Object.class)
                .name("com.ssdl.handle.core.GenerateDynamicsReportFieldsClass");
        for (int i = 0; i < titles.size(); i++) {
//            DynamicsReportQueryField title = (DynamicsReportQueryField)titles.get(i);
//            if (title.getFieldName() == null || title.getFieldCode() == null) {
//                throw new IllegalArgumentException("Dynamics Report Query Field fieldName fieldCode is null");
//            }
//            AnnotationDescription exampleAnnotation = AnnotationDescription.Builder
//                    .ofType(McExcelProperty.class)
//                    .define("value", title.getFieldName())
//                    .define("order",i)
//                    .build();
//            builder = builder
//                    .defineField(title.getFieldCode(), String.class)
//                    .annotateField(exampleAnnotation);
        }

        Class<?> dynamicType = builder
                .make()
                .load(GenerateDynamicsReportFieldsClassUtils.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        return dynamicType;
    }

}
