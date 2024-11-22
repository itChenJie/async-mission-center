package com.mission.center.server.xxljob;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mission.center.constant.IeTaskState;
import com.mission.center.server.entity.McIeTask;
import com.mission.center.server.executor.AsyncTaskService;
import com.mission.center.server.mapper.McIeTaskMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 任务执行定时任务
 * @Description
 **/
@Component
@RequiredArgsConstructor
public class IeTaskJob {
    private final McIeTaskMapper mcIeTaskMapper;

    private final AsyncTaskService asyncTaskService;
    /**
     * 任务执行 每2分钟执行一次
     * @return
     */
    @XxlJob("mission-center-export-task")
    public ReturnT<String> exportTask(){
        List<McIeTask> mcIeTasks = mcIeTaskMapper.selectList(new LambdaQueryWrapper<McIeTask>().in(McIeTask::getState
                        , IeTaskState.WAIT_START.getValue(), IeTaskState.FILE_UPLOADING.getValue(),IeTaskState.STOP.getValue())
                .le(McIeTask::getExecutionTime,new Date()));
        if (CollUtil.isEmpty(mcIeTasks))
            return ReturnT.SUCCESS;


        for (McIeTask mcIeTask : mcIeTasks) {
            asyncTaskService.execute(mcIeTask);
        }
        return ReturnT.SUCCESS;
    }
}
