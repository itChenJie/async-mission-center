package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IeTaskTemplateRequest extends BaseRequest{
    @ApiModelProperty(value = "模版编码",required = true)
    @NotBlank(message = "模版编码不能为空！")
    private String code;
}
