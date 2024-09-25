package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BasePageRequest extends BaseRequest{
    @ApiModelProperty(value = "分页",required = true)
    @NotNull(message = "分页 不能为空！")
    private Integer page=1;

    @ApiModelProperty(value = "分页大小",required = true)
    @NotNull(message = "分页大小 不能为空！")
    private Integer pageSize=10;
}
