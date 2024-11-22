package com.mission.center.server.template;

import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.core.bean.DataHandleBean;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.template.impl.AbstractCleanseTemplate;
import com.mission.center.util.Page;
import com.mission.center.util.PageTotal;
import com.mission.center.util.Pair;

import java.util.Date;
import java.util.List;

/**
 * @Description 清洗数据执行demo
 **/
public class DemoCleanseTemplate extends AbstractCleanseTemplate {
    /**
     * 数据处理
     *
     * @param taskCode 任务编号
     * @param t        待处理数据
     * @return 成功、失败条数
     */
    @Override
    public DataHandleBean cleanse(String taskCode, List t) {
        return null;
    }

    /**
     * 计算分页信息
     *
     * @param taskCode           任务编码
     * @param queryConditionJson 参数json
     * @param shardingSize       分页大小
     * @return 计算分页信息
     */
    @Override
    public PageTotal count(String taskCode, String queryConditionJson, Integer shardingSize) {
        PageTotal pageTotal = PageTotal.of(Math.toIntExact(100l), shardingSize);
        return pageTotal;
    }

    /**
     * 分页查询
     *
     * @param context  上下文请求
     * @param page     页
     * @param cursorId 当前滚动的分页的游标ID,可以是使用ID 做最大最小值传递。主要是用于优化传递分页查询速度
     * @return 当前最大ID key: cursorId,value: resultList
     */
    @Override
    public Pair<Long, List> shardingData(TaskRequestContext context, Page page, Long cursorId) {
        return Pair.of(cursorId, null);
    }

    /**
     * 模版名称
     *
     * @return
     */
    @Override
    public String name() {
        return "清洗数据执行demo";
    }

    /**
     * 模块编码
     *
     * @return
     */
    @Override
    public ServiceModuleEnum moduleCode() {
        return ServiceModuleEnum.ERP;
    }

    /**
     * 处理完成
     *
     * @param taskCode 任务编码
     * @return
     */
    @Override
    public void processingFinish(String taskCode) {

    }
}
