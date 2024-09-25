package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IeTaskTemplateResponse {
    @ApiModelProperty(value = "模版名称")
    private String name;

    @ApiModelProperty(value = "模版编码")
    private String code;
}
