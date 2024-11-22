package com.mission.center.core.task;


import com.mission.center.core.bean.UpdateNumberBean;
import com.mission.center.core.bean.UpdateStateBean;

import java.util.Date;

public interface IIeTaskService {
    /**
     * 更新任务状态
     * @param code
     * @param stateBean
     * @return
     */
    public boolean updateState(String code, UpdateStateBean stateBean);

    /**
     * 更新任务进度
     * @param code 任务编码
     * @param schedule 进度
     * @param currentPage 当前分页
     * @param currentPageTheIndex 当前分页内下标
     * @return
     */
    public boolean updateSchedule(String code,Integer schedule,Integer currentPage,Integer currentPageTheIndex);

    /**
     * 更新任务文件远程地址
     * @param ossKey
     * @param code
     */
    void updateFileRemoteAddress(String ossKey, String code);

    /**
     * 更新文件名称
     * @param taskCode
     * @param fileName
     */
    void updateFileName(String taskCode, String fileName);

    /**
     * 更新处理数据条数
     *
     * @param code
     * @param numberBean
     */
    void updateNumber(String code, UpdateNumberBean numberBean);

    /**
     * 保存临时文件存储 服务器ip
     * @param taskCode
     * @param ip
     */
    void updateTempFileSaveIp(String taskCode, String ip);

    /**
     * 任务挂起
     * @param taskCode
     * @param nextExecutionTime
     */
    void hangUpTask(String taskCode, Date nextExecutionTime);
}
