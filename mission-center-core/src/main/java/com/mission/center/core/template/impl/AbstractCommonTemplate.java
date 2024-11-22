package com.mission.center.core.template.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constants.Constants;
import com.mission.center.core.bean.UpdateNumberBean;
import com.mission.center.core.bean.UpdateStateBean;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.entity.ResponseWrapper;
import com.mission.center.error.ServerCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;

/**
 * @Description 模版抽象 公共类
 **/
@Slf4j
public abstract class AbstractCommonTemplate {
    protected IIeTaskService iIeTaskService;

    /**
     * 更新任务总数
     * @param code
     * @param total
     */
    protected void updateTotal(String code,Integer total){
        iIeTaskService.updateNumber(code,new UpdateNumberBean(Long.valueOf(total)));
    }

    /**
     * 更新进度
     * @param code
     * @param currentPage
     * @param totalPage
     * @param currentPage
     */
    protected void updateProcess(String code,Integer currentPage,Integer totalPage){
        double process = (currentPage + 1) / (totalPage * Constants.DOUBLE_ONE);
        int executeProcess = (int) (process * Constants.FULL_PROCESS);
        iIeTaskService.updateSchedule(code, executeProcess,currentPage+1,0);
    }

    /**
     * 更新进度
     * @param code
     * @param currentPage
     * @param totalPage
     * @param currentPage
     * @param currentPageInIndex
     */
    protected void updateProcess(String code,Integer currentPage,Integer currentPageInIndex,Integer totalPage){
        double process = (currentPage + 1) / (totalPage * Constants.DOUBLE_ONE);
        int executeProcess = (int) (process * Constants.FULL_PROCESS);
        iIeTaskService.updateSchedule(code, executeProcess,currentPage+1,currentPageInIndex);
    }

    /**
     * 完成任务处理
     * @param code
     * @param successNumber
     * @param failNumber
     */
    protected void complete(String code,Long successNumber, Long failNumber){
        iIeTaskService.updateNumber(code,new UpdateNumberBean( successNumber, failNumber));
        iIeTaskService.updateState(code, new UpdateStateBean(IeTaskState.SUCCESS));
    }

    /**
     * 失败任务处理
     * @param code
     * @param message
     */
    protected void fail(String code,String message) {
        iIeTaskService.updateState(code, new UpdateStateBean(IeTaskState.FAIL, null, message));
    }

    protected static void checkCountResponse(ResponseWrapper wrapper) {
        Assert.isFalse(!ServerCode.SUCCESS.getCode().equals(wrapper.getCode()), "查询总数失败："+ wrapper.getDesc());
        Assert.isFalse(ObjectUtil.isEmpty(wrapper.getData()), "查询总数失败 数据为空！");
    }

    protected static void checkPageResponse(ResponseWrapper wrapper) {
        Assert.isFalse(!ServerCode.SUCCESS.getCode().equals(wrapper.getCode()), "查询当前页数据失败："+wrapper.getDesc());
        Assert.isFalse(ObjectUtil.isEmpty(wrapper.getData()), "查询当前页数据失败 数据为空！");
    }

    /**
     * 任务挂起
     * @param taskCode
     * @param nextExecutionTime
     */
    protected void hangUpTask(String taskCode, Date nextExecutionTime) {
        iIeTaskService.hangUpTask(taskCode,nextExecutionTime);
    }
}
