package com.mission.center.core.executor.excel;

import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.excel.WaterMarkExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

/**
 * 增强水印
 **/
@Slf4j
public class WaterMarkExcelIntensifier implements ExcelIntensifier {
    private String content="敏感内容";

    public WaterMarkExcelIntensifier() {
    }

    public WaterMarkExcelIntensifier(String content) {
        this.content = content;
    }

    @Override
    public void enhance(Workbook workbook, TaskRequestContext context) {

        if (workbook instanceof XSSFWorkbook) {
            try {
                WaterMarkExcelUtil.printWaterMark((XSSFWorkbook) workbook, content);
            } catch (IOException e) {
                log.error("[WaterMarkExcelIntensifier#enhance]", e);
            }
        } else if (workbook instanceof SXSSFWorkbook) {
            try {
                WaterMarkExcelUtil.printWaterMark((SXSSFWorkbook) workbook, content);
            } catch (IOException e) {
                log.error("[WaterMarkExcelIntensifier#enhance]", e);
            }
        } else {
            throw new RuntimeException("this workbook not support watermark!");
        }
    }
}
