package com.mission.center.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constant.IeTaskType;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.serializer.CommonEnumDeserializer;
import com.mission.center.serializer.EnumValueSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class IeTaskListBean {
    @ApiModelProperty(value = "任务编号")
    private String code;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "任务类型")
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    private IeTaskType type;

    @ApiModelProperty(value = "所属系统")
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    private ServiceModuleEnum moduleCode;

    @ApiModelProperty(value = "导出模块名称")
    private String templateName;

    @ApiModelProperty(value = "状态 ")
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    private IeTaskState state;

    @ApiModelProperty(value = "文件服务器key")
    private String fileKey;

    @ApiModelProperty(value = "进度")
    private Integer schedule;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "创建人")
    private String createName;

    @ApiModelProperty(value = "导出人用户标识")
    private String serviceModelUserId;

    @ApiModelProperty(value = "创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "完成时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    @ApiModelProperty(value = "成功数")
    private Long successNumber;

    @ApiModelProperty(value = "失败数")
    private Long failNumber;

    @ApiModelProperty(value = "总条数")
    private Long totalNumber;

    @ApiModelProperty(value = "执行时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date executionTime;
}
