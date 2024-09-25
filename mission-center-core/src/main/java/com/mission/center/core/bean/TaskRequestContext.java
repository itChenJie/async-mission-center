package com.mission.center.core.bean;

import com.mission.center.constant.IeTaskState;
import com.mission.center.core.task.IIeTaskService;
import lombok.Data;

import java.io.OutputStream;


@Data
public class TaskRequestContext {
    private OutputStream out;
    /**
     * 任务处理bean
     */
    private IIeTaskService iIeTaskService;
    /**
     * 查询条件json
     */
    private String queryConditionJson;

    private String userId;
    /**
     * 任务编码
     */
    private String taskCode;
    /**
     * 模版编码
     */
    private String templateCode;

    private Integer shardingSize;

    private IeTaskState state;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 当前执行分页数
     */
    private Integer currentPage;
}
