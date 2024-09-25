package com.mission.center.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "导出导入任务添加")
public class IeTaskAddRequest extends BaseRequest{
    @ApiModelProperty(value = "模版编码",required = true)
    @NotBlank(message = "模版编码 不能为空！")
    private String templateCode;

    @ApiModelProperty(value = "业务系统用户唯一标识",required = true)
    @NotBlank(message = "业务系统用户唯一标识 不能为空！")
    private String userId;

    @ApiModelProperty(value = "导入文件名 数据导入任务必传")
    private String fileName;

    @ApiModelProperty(value = "导入文件地址 数据导入任务必传")
    private String importFileKey;

    @ApiModelProperty(value = "查询条件json",required = true)
    @NotBlank(message = "查询条件json 不能为空！")
    private String queryConditionJson;

    @ApiModelProperty(value = "菜单编码",required = true)
    @NotBlank(message = "菜单编码不能为空！")
    private String menuCode;

    @ApiModelProperty(value = "导入、数据处理本次任务数据详情列表页面路由地址")
    private String detailsUrl;

    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

    @ApiModelProperty(value = "延迟执行 默认否")
    private boolean delayExecution;
}
