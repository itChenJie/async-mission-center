package com.mission.center.core.executor.excel;

import com.mission.center.core.bean.TaskRequestContext;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel增强器
 */
public interface ExcelIntensifier {

    /**
     * 是否启动
     * @param context context
     * @return 是否启用
     */
    default boolean enable(TaskRequestContext context){
        return true;
    }

    /**
     * 增强Excel workbook
     * @param workbook workbook
     * @param context context
     */
    void enhance(Workbook workbook, TaskRequestContext context);

}