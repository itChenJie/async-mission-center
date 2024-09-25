package com.mission.center.core.template;

import com.mission.center.constant.IeTaskType;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.core.bean.TaskRequestContext;

import java.util.Date;

/**
 * 任务模版基础接口
 */
public interface TaskTemplate {

    /**
     * 模版名称
     * @return
     */
    String name();

    /**
     * 模块编码
     * @return
     */
    ServiceModuleEnum moduleCode();

    /**
     * 执行
     * @param context
     */
    boolean execute(TaskRequestContext context);

    /**
     * 是否启用导出缓存
     * @param request
     * @return
     */
    default boolean enableExportCache(Object request){return false;}

    /**
     * 任务类型
     * @return
     */
    IeTaskType taskType();

    /**
     * 获取可执行时间
     * @return 默认当前时间，自定义模版根据业务要求自定义
     */
    default Date executionTime(){return  new Date();}

    /**
     * 可执行区间 24小时制
     * 示例：0-5,20-23
     * @return null 不存在
     */
    default String executionSection(){return null;}

    /**
     * 处理完成
     * @param taskCode 任务编码
     * @return
     */
    default void processingFinish(String taskCode){return ;}
}
