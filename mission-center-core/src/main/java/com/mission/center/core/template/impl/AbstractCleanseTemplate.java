package com.mission.center.core.template.impl;

import com.mission.center.core.bean.DataHandleBean;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.template.ICleanseTemplate;
import com.mission.center.util.Page;
import com.mission.center.util.PageTotal;
import com.mission.center.util.PageUtil;
import com.mission.center.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 数据处理模版 基类
 *
 * @param <T>
 */
@Slf4j
public abstract class AbstractCleanseTemplate<T> extends AbstractCommonTemplate implements ICleanseTemplate<T> {

    /**
     * 执行
     * @param context
     * @return
     */
    @Override
    public boolean execute(TaskRequestContext context) {
        iIeTaskService = context.getIIeTaskService();
        try {
            // 获取总数
            PageTotal pageTotal = count(context.getTaskCode(),context.getQueryConditionJson(), context.getShardingSize());
            log.info("pageTotal-------------------:{}",pageTotal);
            log.info("TaskRequestContext-------------------:{}",context);
            if (pageTotal.getTotal() <= 0) {
                complete(context.getTaskCode(), 0l, 0l);
                return true;
            }
            updateTotal(context.getTaskCode(),pageTotal.getTotal());
            // 查询分页数据
            int totalPage = PageUtil.totalPage(pageTotal.getTotal(), pageTotal.getPageSize());
            Page page = new Page(1, pageTotal.getPageSize(), pageTotal.getTotal());
            Long cursorId = 0L;
            Long successNumber = 0l;
            Long failNumber = 0l;
            Integer currentPageInIndex = context.getCurrentPageInIndex();
            boolean isFirst = true;
            // 任务再次开启 从上次处理的位置开始
            for (int i = context.getCurrentPage(); i < totalPage; i++) {
                page.setPageNum(i + 1);
                Pair<Long, List<T>> pair = shardingData(context, page, cursorId);
                if (Objects.isNull(pair))
                    continue;
                cursorId = pair.getKey();
                List<T> value = pair.getValue();
                // 第一次进来并且当前分页内数据下标大于0 ，从当前下标重新开始
                if (isFirst&&currentPageInIndex>0){
                    value =value.subList(currentPageInIndex,value.size());
                    isFirst =false;
                }
                DataHandleBean handleBean = cleanse(context.getTaskCode(),value);
                successNumber += handleBean.getSuccessNumber();
                failNumber += handleBean.getFailNumber();
                // 如果是挂起，则回退一页
                Date nextExecutionTime = handleBean.getNextExecutionTime();
                i=Objects.nonNull(nextExecutionTime)?i-1:i;
                updateProcess(context.getTaskCode(),i,handleBean.getIndex(),totalPage);
                // 任务挂起
                if (Objects.nonNull(nextExecutionTime)){
                    hangUpTask(context.getTaskCode(),nextExecutionTime);
                }
            }
            complete(context.getTaskCode(), successNumber, failNumber);
            processingFinish(context.getTaskCode());
        } catch (Exception e) {
            e.printStackTrace();
            fail(context.getTaskCode(),e.getMessage());
            return false;
        }
        return true;
    }
}
