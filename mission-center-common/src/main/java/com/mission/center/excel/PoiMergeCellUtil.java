package com.mission.center.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 纵向合并单元格工具类
 */
@Slf4j
public final class PoiMergeCellUtil {

    /**
     * 从 fistRow 到 lastRow
     * 从 firstCol 到 lastCol
     *
     * @param sheet sheet
     * @param firstRow firstRow
     * @param lastRow lastRow
     * @param firstCol firstCol
     * @param lastCol lastCol
     */
    public static void addMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        try {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        } catch (Exception e) {
            log.error("发生了一次合并单元格错误,{},{},{},{}", firstRow, lastRow, firstCol, lastCol);
            log.error(e.getMessage(), e);
        }
    }

}