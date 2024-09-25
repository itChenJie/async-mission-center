package com.mission.center.excel.bean;

import com.mission.center.excel.annotations.McExcelProperty;
import com.mission.center.util.ReflectionUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;

@Data
public class ExcelFiled {
    private Field field;

    private McExcelProperty excelProperty;
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

    public ExcelFiled(Field field, McExcelProperty excelProperty) {
        this.field = field;
        this.excelProperty = excelProperty;
        this.collection = ReflectionUtils.isCollection(field.getType());
        this.customBean = !ReflectionUtils.isJavaClass(field.getType()) && !collection;
    }
}
