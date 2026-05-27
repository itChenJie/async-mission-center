package com.mission.center.server.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * 状态转换器
 * 将 status 整数值转换为可读文字
 * 0 -> 待处理
 * 1 -> 处理中
 * 2 -> 已完成
 */
public class StatusConverter implements Converter<Integer> {

    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 导入时：Excel字符串转Java Integer
     */
    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                      GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return null;
        }
        switch (stringValue.trim()) {
            case "待处理":
                return 0;
            case "处理中":
                return 1;
            case "已完成":
                return 2;
            default:
                // 如果是纯数字字符串，直接解析
                try {
                    return Integer.parseInt(stringValue.trim());
                } catch (NumberFormatException e) {
                    return null;
                }
        }
    }

    /**
     * 导出时：Java Integer转Excel字符串
     */
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                                GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("未知");
        }
        String text;
        switch (value) {
            case 0:
                text = "待处理";
                break;
            case 1:
                text = "处理中";
                break;
            case 2:
                text = "已完成";
                break;
            default:
                text = "未知";
        }
        return new WriteCellData<>(text);
    }
}