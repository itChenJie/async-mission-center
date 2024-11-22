package com.mission.center.core.executor.excel;

import com.mission.center.core.bean.TaskRequestContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collections;
import java.util.List;

/**
 * Excel增强器
 **/
public interface ExcelIntensifierExecutor {

    /**
     * 增强Excel
     *
     * @return excel增强器
     */
    default List<ExcelIntensifier> enhanceExcel() {
        return Collections.emptyList();
    }

    /**
     * 增强Excel
     *
     * @param workbook workbook
     * @param context context
     */
    default void executeEnhance(Workbook workbook, TaskRequestContext context) {
        if (CollectionUtils.isEmpty(enhanceExcel())) {
            return;
        }
        enhanceExcel().stream()
            .filter(e -> e.enable(context))
            .forEach(k -> k.enhance(workbook, context));
    }

}