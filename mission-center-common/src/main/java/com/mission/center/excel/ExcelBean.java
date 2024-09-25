package com.mission.center.excel;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ExcelBean implements Closeable {

    public static final String DEFAULT_SHEET_GROUP = "SHEET";
    /**
     * 切分sheet 最大行数
     */
    private int segmentationSheetRows = 100000;
    /**
     * 基础 Cell Style
     */
    private CellStyle baseStyle;

    /**
     * 工作簿
     */
    private Workbook workbook;

    /**
     * 当前Sheet
     * key: sheet-group ---> value: currentSheet
     */
    private Map<String, SheetBean> currentSheetMap = new ConcurrentHashMap<>();

    /**
     * 当前workbook 总行数
     */
    private int totalRows;

    /**
     * 关闭流
     */
    @Override
    public void close() {
        if (Objects.nonNull(workbook)) {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
        }
    }
    public ExcelBean(Integer segmentationSheetRows) {
        this.segmentationSheetRows = segmentationSheetRows;
    }

    public Workbook getWorkbook() {
        return Objects.nonNull(workbook) ? workbook : new SXSSFWorkbook(ExcelGenProperty.getRowAccessWindowSize());
    }

    /**
     * 获取当前执行的Sheet
     */
    public Sheet getCurrentSheet(String sheetGroup) {
        SheetBean sheetBean = getSheetBean(sheetGroup);
        if (Objects.isNull(sheetBean.getCurrentSheet()) || sheetBean.getCurrentRowIndex() >= segmentationSheetRows) {
            return nextSheet(sheetGroup);
        }
        return sheetBean.getCurrentSheet();
    }

    /**
     * 获取下一页
     */
    private Sheet nextSheet(String sheetGroup) {
        SheetBean sheetBean = getSheetBean(sheetGroup);
        Sheet sheet = workbook
            .createSheet(sheetGroup + (sheetBean.getSheetIndex() > 0 ? "-" + sheetBean.getSheetIndex() : ""));
        sheetBean.nextSheet(sheet);
        return sheet;
    }

    /**
     * 写入行数
     */
    public void writeRow(int rows, String sheetGroup) {
        SheetBean sheetBean = getSheetBean(sheetGroup);
        totalRows += rows;
        sheetBean.writeRows(rows);
    }

    /**
     * 获取SheetBean
     *
     * @param sheetGroup sheetGroup
     * @return SheetBean
     */
    private SheetBean getSheetBean(String sheetGroup) {
        SheetBean sheetBean = currentSheetMap.get(sheetGroup);
        if (Objects.nonNull(sheetBean)) {
            return sheetBean;
        }
        sheetBean = new SheetBean();
        currentSheetMap.put(sheetGroup, sheetBean);
        return sheetBean;
    }

    /**
     * 克隆style
     */
    public CellStyle cloneStyleByBase() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(baseStyle);
        return cellStyle;
    }

    public int getCurrentRowIndex(String sheetGroup) {
        return getSheetBean(sheetGroup).getCurrentRowIndex();
    }

    public void logExportInfo(Logger log) {
        StringBuilder builder = new StringBuilder();

        for (Entry<String, SheetBean> entry : this.currentSheetMap.entrySet()) {
            builder.append(" [sheet:");
            builder.append(entry.getKey());
            builder.append(",");
            builder.append("sumRow:");
            builder.append(entry.getValue().getSumRow());
            builder.append("] ");
        }
        if (log.isDebugEnabled()) {
            log.debug("[ExcelBean#executeFinish] totalRows:{},Sheet:{}", totalRows, builder);
        }
    }

    /**
     * 设置基本的表头标题格式
     *
     * @return cellStyle
     */
    public CellStyle decorateHeader() {
        CellStyle cellStyle = this.cloneStyleByBase();
        Font font = this.getWorkbook().createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        return cellStyle;
    }
}