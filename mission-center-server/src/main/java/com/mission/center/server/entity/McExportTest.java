package com.mission.center.server.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mission.center.excel.annotations.McColumnWidth;
import com.mission.center.server.converter.StatusConverter;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 导出测试实体
 */
@Data
@TableName("mc_export_test")
public class McExportTest {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "用户名", order = 1)
    @McColumnWidth(3000)
    private String userName;

    @ExcelProperty(value = "邮箱", order = 2)
    @McColumnWidth(5000)
    private String email;

    @ExcelProperty(value = "手机号", order = 3)
    @McColumnWidth(4000)
    private String phone;

    @ExcelProperty(value = "状态", order = 4, converter = StatusConverter.class)
    @McColumnWidth(2000)
    private Integer status;

    @ExcelProperty(value = "创建时间", order = 5)
    @McColumnWidth(5000)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ExcelProperty(value = "金额", order = 6)
    @McColumnWidth(3000)
    private BigDecimal amount;

    @ExcelProperty(value = "描述", order = 7)
    @McColumnWidth(6000)
    private String description;
}