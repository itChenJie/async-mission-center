package com.mission.center.core.utils;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.converters.AutoConverter;
import com.alibaba.excel.converters.Converter;
import com.mission.center.error.ServerCode;
import com.mission.center.error.ServiceException;
import com.mission.center.excel.annotations.McColumnWidth;
import com.mission.center.excel.bean.ExcelFiled;
import com.mission.center.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class ExcelBeanUtils {
    /**
     * 导出实体，字段属性缓存
     */
    private static final Map<Class<?>, LinkedHashSet<ExcelFiled>> GROUP_BY_FIELD_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 动态数据报表导出类解析 TODO
     * @param titles
     * @return
     * @param <T>
     */
    public static <T> List<ExcelFiled> getDynamicExcelFiledAnalyze(List<Object> titles) {
        Assert.notNull(titles,()-> new ServiceException("导出数据报表标题空！"));
        Class generateClass = GenerateDynamicsReportFieldsClassUtils.generateClass(titles);
        return getExcelFileAnalyze(generateClass);
    }

    /**
     * 通用报表导出类解析 按照分组获取导出工具字段类
     *
     * @param sourceClazz class
     * @param group 分组
     * @param <T> T
     * @return excel 字段
     */
    public static <T> List<ExcelFiled> getExcelFileAnalyze(Class<T> sourceClazz, Class<?>... group) {
        Assert.notNull(sourceClazz, ()-> new ServiceException(ServerCode.PARAM_ERROR));

        LinkedHashSet<ExcelFiled> excelFiledList = loadCache(sourceClazz, sClazz -> {
            List<Field> fields = getClassFields(sClazz);
            return fields.stream().map(field -> {
                        ExcelIgnore ignore = field.getDeclaredAnnotation(ExcelIgnore.class);
                        if (!Objects.isNull(ignore)) {
                            return null;
                        }

                        ExcelFiled excelFiled = createExcelFiled(field);
                        if (excelFiled == null) {
                            return null;
                        }

                        Class<?> subClass = null;
                        boolean existedSub = false;
                        if (ReflectionUtils.isCollection(field.getType())) {
                            existedSub = true;
                            if (ReflectionUtils.isCollection(field.getType()) &&
                                    field.getGenericType() instanceof ParameterizedType) {
                                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                                Type[] types = genericType.getActualTypeArguments();
                                if (Objects.nonNull(types) && types.length == 1 && types[0] instanceof Class) {
                                    subClass = (Class<?>) types[0];
                                }
                            }
                        } else if (!ReflectionUtils.isBaseJavaClass(field.getType())) {
                            subClass = field.getType();
                            existedSub = true;
                        }
                        // 如果存在子字段，则获取子字段
                        if (existedSub && Objects.nonNull(subClass)) {
                            List<ExcelFiled> subFieldList = getClassFields(subClass).stream()
                                    .map(e -> createExcelFiled(e))
                                    .filter(Objects::nonNull)
                                    .sorted((Comparator.comparingInt(ExcelFiled::getOrder)))
                                    .collect(Collectors.toList());
                            excelFiled.setSubFiledList(subFieldList);
                            if (CollectionUtils.isEmpty(subFieldList)) {
                                return null;
                            }
                        }
                        return excelFiled;
                    }).filter(Objects::nonNull)
                    .sorted((Comparator.comparingInt(ExcelFiled::getOrder)))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        });

//        // 分组
//        Set<Class<?>> groupSet;
//        if (group == null || group.length == 0) {
//            groupSet = new HashSet<>();
//            groupSet.add(Default.class);
//        } else {
//            groupSet = new HashSet<>(CommCollectionUtils.newArrayList(group));
//        }
//
//        //获取所有的包含分组的字段
//        List<ExcelFiled> filedList = excelFiledList.stream().filter(e -> {
//                    // 如果当前符合条件了直接进行判断子字段是否符合
//                    boolean groupField = isBelongGroup(e, groupSet);
//                    if (groupField) {
//                        List<ExcelFiled> subFieldList = Optional.ofNullable(e.getSubFiledList())
//                                .orElse(Collections.emptyList())
//                                .stream()
//                                .filter(subFiled -> isBelongGroup(subFiled, groupSet))
//                                .collect(Collectors.toList());
//                        e.setSubFiledList(subFieldList);
//                    }
//                    return groupField;
//                }).sorted(Comparator.comparingInt(o -> o.getExcelProperty().order()))
//                .collect(Collectors.toList());

        // 分组暂不处理，直接返回所有字段
        List<ExcelFiled> filedList = new ArrayList<>(excelFiledList);

        // 计算下标
        calculateColumnIndex(filedList);
        return filedList;
    }

    /**
     * 创建 ExcelFiled 对象
     * @param field 字段
     * @return ExcelFiled 或 null（如果字段没有 @ExcelProperty 注解）
     */
    private static ExcelFiled createExcelFiled(Field field) {
        ExcelProperty excelProperty = getAnnotatedExcelProperty(field);
        if (excelProperty == null) {
            return null;
        }

        McColumnWidth columnWidth = field.getDeclaredAnnotation(McColumnWidth.class);
        DateTimeFormat dateTimeFormat = field.getDeclaredAnnotation(DateTimeFormat.class);

        ExcelFiled excelFiled = new ExcelFiled(field, excelProperty, columnWidth, dateTimeFormat);

        // 初始化 converter
        Class<? extends Converter> converterClass = excelProperty.converter();
        if (converterClass != null && converterClass != AutoConverter.class) {
            try {
                Converter<Object> converter = (Converter<Object>) converterClass.newInstance();
                excelFiled.setConverterInstance(converter);
            } catch (Exception e) {
                log.warn("converter instance create failed for field: {}", field.getName(), e);
            }
        }

        return excelFiled;
    }

    private static LinkedHashSet<ExcelFiled> loadCache(Class<?> sourceClazz, java.util.function.Function<Class<?>, LinkedHashSet<ExcelFiled>> cacheLoader) {
        LinkedHashSet<ExcelFiled> resultMap = GROUP_BY_FIELD_CACHE_MAP.get(sourceClazz);
        if (Objects.isNull(resultMap)) {
            LinkedHashSet<ExcelFiled> cacheSet = cacheLoader.apply(sourceClazz);
            GROUP_BY_FIELD_CACHE_MAP.put(sourceClazz, cacheSet);
            return cacheSet;
        }
        return resultMap;
    }

    /**
     * 获取父类的字段
     * @return {@link List<Field>}
     */
    public static List<Field> getClassFields(Class<?> clazz) {
        List<Field> list = new ArrayList<>();
        Set<String> filedNameSet = new HashSet<>();
        Field[] fields;
        do {
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!filedNameSet.contains(field.getName())) {
                    list.add(field);
                    filedNameSet.add(field.getName());
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class && clazz != null);
        return list;
    }

    /**
     * 获取被 ExcelProperty 注解标注的注解
     *
     * @param field 属性字段
     * @return ExcelProperty
     */
    private static ExcelProperty getAnnotatedExcelProperty(Field field) {
        ExcelProperty property = field.getDeclaredAnnotation(ExcelProperty.class);
        if (Objects.nonNull(property)) {
            return property;
        }
        return AnnotatedElementUtils.findMergedAnnotation(field, ExcelProperty.class);
    }

    /**
     * 计算ExcelFiled 对应的下标
     * @param filedList 属性字段
     */
    private static void calculateColumnIndex(List<ExcelFiled> filedList) {
        if (CollectionUtils.isEmpty(filedList)) {
            return;
        }

        int currentColumnIndex = -1;
        for (ExcelFiled excelFiled : filedList) {
            excelFiled.setFrmColumnIndex(++currentColumnIndex);
            if (CollectionUtils.isNotEmpty(excelFiled.getSubFiledList())) {
                excelFiled.setToColumnIndex(excelFiled.getFrmColumnIndex() + excelFiled.getSubFiledList().size() - 1);
                int subFrmColumnIndex = excelFiled.getFrmColumnIndex() - 1;
                for (ExcelFiled subField : excelFiled.getSubFiledList()) {
                    subField.setFrmColumnIndex(++subFrmColumnIndex);
                    subField.setToColumnIndex(subField.getFrmColumnIndex());
                }
                currentColumnIndex += excelFiled.getSubFiledList().size() - 1;
            }
        }
    }
}
