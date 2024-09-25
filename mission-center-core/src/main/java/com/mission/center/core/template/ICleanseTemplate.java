package com.mission.center.core.template;

import com.mission.center.constant.IeTaskType;
import com.mission.center.core.bean.DataHandleBean;

import java.util.List;

/**
 * @Description 数据处理基础模版接口
 */
public interface ICleanseTemplate<T> extends PageShardingTemplate<T>{

    /**
     * 数据处理
     * @param taskCode 任务编号
     * @param t 待处理数据
     * @return 成功、失败条数
     */
    DataHandleBean cleanse(String taskCode, List<T> t);

    /**
     * 任务类型
     *
     * @return
     */
    @Override
    default IeTaskType taskType(){ return IeTaskType.CLEANSE;}
}
