package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IeTaskRequest {
    @ApiModelProperty(value = "任务编码")
    @NotBlank(message = "任务编码不能为空！")
    private String taskCode;
}
