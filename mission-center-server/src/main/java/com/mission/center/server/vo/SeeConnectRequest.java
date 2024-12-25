package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeeConnectRequest extends BaseRequest{
    @NotBlank(message = "用户id 不能为空！")
    @ApiModelProperty(value = "用户id",required = true)
    private String userId;

    @NotBlank(message = "系统标识 不能为空！")
    @ApiModelProperty(value = "系统 标识",required = true)
    private String system;

}
