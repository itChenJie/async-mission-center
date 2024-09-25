package com.mission.center.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.serializer.CommonEnumDeserializer;
import com.mission.center.serializer.EnumValueSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "IeTaskTemplateListRequest", description = "导出任务中心任务模板列表")
public class IeTaskTemplateListRequest extends BaseRequest{
    @ApiModelProperty(value = "业务模块标识",required = true)
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    @NotNull(message = "业务模块标识 不能为空！")
    private ServiceModuleEnum moduleCode;
}
