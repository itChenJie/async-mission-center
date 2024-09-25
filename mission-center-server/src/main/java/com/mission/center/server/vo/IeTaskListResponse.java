package com.mission.center.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "IeTaskListResponse", description = "导出导入任务列表响应")
@Data
public class IeTaskListResponse {
    @ApiModelProperty("订单列表")
    private List<IeTaskListBean> taskList;
    @ApiModelProperty("总条数")
    private long total;
}
