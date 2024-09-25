package com.mission.center.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.mission.center.annotation.TimeIntervalValidate;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constant.IeTaskType;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.constants.TimeIntervalFieldEnum;
import com.mission.center.constants.TimeUnit;
import com.mission.center.serializer.CommonEnumDeserializer;
import com.mission.center.serializer.EnumValueSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(value = "导出导入任务列表入参")
public class IeTaskListRequest extends BasePageRequest{
    @ApiModelProperty(value = "任务编号")
    private String code;

    @ApiModelProperty(value = "文件名称")
    @Size(max = 50,message = "文件名称限制50个字符")
    private String fileName;

    @ApiModelProperty(value = "所属系统",required = true)
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    @NotNull(message = "所属系统 不能为空！")
    private ServiceModuleEnum moduleCode;

    @ApiModelProperty(value = "菜单编码")
    private String menuCode;

    @ApiModelProperty(value = "任务模版编码")
    private String templateCode;

    @ApiModelProperty(value = "任务类型")
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    private List<IeTaskType> types;

    @ApiModelProperty(value = "状态 0待启动 1执行中 2文件待上传 3失败 4成功")
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    private IeTaskState state;

    @ApiModelProperty(value = "任务创建人标识" ,required = true)
    @NotBlank(message = "创建人不能为空！")
    private String serviceModelUserId;

    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)",required = true)
    @TimeIntervalValidate(intervalType = TimeUnit.DAY,interval = 31,type = TimeIntervalFieldEnum.START)
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)",required = true)
    @TimeIntervalValidate(intervalType = TimeUnit.DAY,interval = 31,type = TimeIntervalFieldEnum.END)
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "渠道编码 通用saas 必传")
    private String channelCode;
}
