package com.mission.center.core.template;

import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.util.Page;
import com.mission.center.util.PageTotal;
import com.mission.center.util.Pair;

import java.util.List;

/**
 * 按页分片下载执行器
 * <T> :
 */
public interface PageShardingTemplate<T> extends TaskTemplate {
    /**
     * 计算分页信息
     * @param taskCode 任务编码
     * @param queryConditionJson 参数json
     * @param shardingSize 分页大小
     * @return 计算分页信息
     */
    PageTotal count(String taskCode, String queryConditionJson, Integer shardingSize);

    /**
     * 分页查询
     * @param context 上下文请求
     * @param page 页
     * @param cursorId 当前滚动的分页的游标ID,可以是使用ID 做最大最小值传递。主要是用于优化传递分页查询速度
     * @return 当前最大ID key: cursorId,value: resultList
     */
    Pair<Long, List<T>> shardingData(TaskRequestContext context, Page page, Long cursorId);

}
