package com.mission.center.excel;

import cn.hutool.core.util.ReflectUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.mission.center.util.FontImageUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 水印工具类
 **/
public class WaterMarkExcelUtil {
    /**
     * 打印水印
     * @param workbook
     * @param content
     * @throws IOException
     */
    public static void printWaterMark(XSSFWorkbook workbook, String content) throws IOException {

        BufferedImage image = FontImageUtils.createWatermarkImage(content, "yyyy-MM-dd HH:mm:ss", "#C5CBCF");
        // 导出到字节流B
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);

        int pictureIdx = workbook.addPicture(os.toByteArray(), Workbook.PICTURE_TYPE_PNG);
        XSSFPictureData pictureData = workbook.getAllPictures().get(pictureIdx);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            String rID = sheet.addRelation(null, XSSFRelation.IMAGES, pictureData).getRelationship().getId();
            sheet.getCTWorksheet().addNewPicture().setId(rID);
        }
    }

    /**
     * 打印水印
     * @param workbook
     * @param content
     * @throws IOException
     */
    public static void printWaterMark(SXSSFWorkbook workbook, String content) throws IOException {
        BufferedImage image = FontImageUtils.createWatermarkImage(content, "yyyy-MM-dd HH:mm:ss", "#C5CBCF");
        // 导出到字节流B
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);

        int pictureIdx = workbook.addPicture(os.toByteArray(), Workbook.PICTURE_TYPE_PNG);

        XSSFPictureData pictureData = (XSSFPictureData) workbook.getAllPictures().get(pictureIdx);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            SXSSFSheet sheet = workbook.getSheetAt(i);
            XSSFSheet xssfSheet = (XSSFSheet) ReflectUtil.getFieldValue(sheet, "_sh");
            String rID = xssfSheet.addRelation(null, XSSFRelation.IMAGES, pictureData).getRelationship().getId();
            xssfSheet.getCTWorksheet().addNewPicture().setId(rID);
        }
    }

}
