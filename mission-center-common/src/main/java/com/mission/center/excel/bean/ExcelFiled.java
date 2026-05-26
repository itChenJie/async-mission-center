package com.mission.center.excel.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.converters.Converter;
import com.mission.center.excel.annotations.McColumnWidth;
import com.mission.center.util.ReflectionUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

@Data
public class ExcelFiled {
    private Field field;

    private ExcelProperty excelProperty;
    private McColumnWidth columnWidth;
    private DateTimeFormat dateTimeFormat;
    private Converter<Object> converterInstance;

    /**
     * 是否是集合
     */
    private boolean collection;
    /**
     * 自定义类型
     */
    private boolean customBean;
    /**
     * 是否包含子字段集合
     */
    private List<ExcelFiled> subFiledList;

    /**
     * 开始列下标
     */
    private int frmColumnIndex = -1;

    /**
     * 结束列下标
     */
    private int toColumnIndex = -1;

    public ExcelFiled(Field field, ExcelProperty excelProperty,
                       McColumnWidth columnWidth, DateTimeFormat dateTimeFormat) {
        this.field = field;
        this.excelProperty = excelProperty;
        this.columnWidth = columnWidth;
        this.dateTimeFormat = dateTimeFormat;
        this.collection = ReflectionUtils.isCollection(field.getType());
        this.customBean = !ReflectionUtils.isJavaClass(field.getType()) && !collection;
    }

    /**
     * 获取表头名
     */
    public String getHeaderName() {
        if (excelProperty != null && excelProperty.value().length > 0) {
            return excelProperty.value()[0];
        }
        return field != null ? field.getName() : "";
    }

    /**
     * 获取列宽
     */
    public int getWidth() {
        return columnWidth != null ? columnWidth.value() : 4096;
    }

    /**
     * 获取排序值
     */
    public int getOrder() {
        return excelProperty != null ? excelProperty.order() : Integer.MAX_VALUE;
    }

    /**
     * 获取日期格式
     */
    public String getDateFormatter() {
        return dateTimeFormat != null ? dateTimeFormat.value() : "yyyy-MM-dd HH:mm:ss";
    }
}