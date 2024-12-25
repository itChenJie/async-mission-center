package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeePushAllRequest extends BaseRequest{
    @NotBlank(message = "消息内容 不能为空！")
    @ApiModelProperty(value = "消息内容",required = true)
    private String message;
}
