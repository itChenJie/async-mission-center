package com.mission.center.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class IeTaskUserListResponse {
    @ApiModelProperty("订单列表")
    private List<IeTaskUserListBean> list;

    @ApiModelProperty(value = "是否支持查询用户 false 展示当前用户，true 展示接口返回")
    private boolean support;
}
