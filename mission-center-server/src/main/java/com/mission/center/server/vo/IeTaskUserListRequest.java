package com.mission.center.server.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.serializer.CommonEnumDeserializer;
import com.mission.center.serializer.EnumValueSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description
 **/
@Data
public class IeTaskUserListRequest extends BaseRequest{
    @ApiModelProperty(value = "所属系统",required = true)
    @JSONField(serializeUsing = EnumValueSerializer.class,deserializeUsing = CommonEnumDeserializer.class)
    @NotNull(message = "所属系统 不能为空！")
    private ServiceModuleEnum moduleCode;

    @ApiModelProperty(value = "用户账号",required = true)
    @NotBlank(message = "用账号不能为空！")
    private String user;
}
