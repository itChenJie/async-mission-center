package com.mission.center.core.template;

import com.mission.center.constant.ExportTemplateType;
import com.mission.center.constant.IeTaskType;

import java.util.List;

/**
 * @Description 导出基础模版接口
 */
public interface IExportTemplate<T> extends PageShardingTemplate<T>{
    /**
     * 模版类型
     * @return
     */
    ExportTemplateType type();

    /**
     * 用户动态报表模版 获取标题
     * @return
     */
    default List<Object> getDynamicsTitles(String queryConditionJson){
        return null;
    }

    /**
     * 任务类型
     *
     * @return
     */
    @Override
    default IeTaskType taskType(){ return IeTaskType.EXPORT;}
}
