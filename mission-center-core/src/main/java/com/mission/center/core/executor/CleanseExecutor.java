package com.mission.center.core.executor;

import cn.hutool.core.lang.Assert;
import com.mission.center.constant.IeTaskState;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.bean.UpdateStateBean;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.core.template.impl.AbstractCleanseTemplate;
import com.mission.center.error.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Description 数据处理执行器
 **/
@Slf4j
public class CleanseExecutor extends Thread{
    private Map<String, AbstractCleanseTemplate> cleanseTemplateMap;
    private TaskRequestContext context;

    public CleanseExecutor(Map<String, AbstractCleanseTemplate> ieDownloadTemplateMap,TaskRequestContext context) {
        this.cleanseTemplateMap = ieDownloadTemplateMap;
        this.context = context;
    }

    @Override
    public void run() {
        log.info("数据处理任务开始执行：{}  state:{}",context.getTaskCode(),context.getState().getValue());
        IIeTaskService iIeTaskService = context.getIIeTaskService();

        boolean updateState = iIeTaskService.updateState(context.getTaskCode(),new UpdateStateBean(IeTaskState.EXECUTING, IeTaskState.WAIT_START));
        Assert.isFalse(!updateState,() ->new ServiceException("当前任务不是待启动无法执行数据处理："+context.getTaskCode()));
        iIeTaskService.updateFileName(context.getTaskCode(),"系统自动处理");
        AbstractCleanseTemplate cleanseTemplate = cleanseTemplateMap.get(context.getTemplateCode());
        if (cleanseTemplate==null){
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.EXECUTING, null,"当前数据处理模版暂不支持！"));
            return;
        }
        cleanseTemplate.execute(context);
    }
}
