package com.mission.center.server.executor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.mission.center.constants.Constants;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.executor.CleanseExecutor;
import com.mission.center.core.executor.ExportExecutor;
import com.mission.center.core.executor.ImportExecutor;
import com.mission.center.core.file.FileService;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.core.template.impl.AbstractCleanseTemplate;
import com.mission.center.core.template.impl.AbstractExportTemplate;
import com.mission.center.error.ServiceException;
import com.mission.center.server.entity.McIeTask;
import com.mission.center.util.TaskUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description 任务中心 任务分配器
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncTaskService {
    private final ThreadPoolExecutor threadPoolExecutor;

    private final Map<String, AbstractExportTemplate> exportTemplateMap;

    private final Map<String, AbstractCleanseTemplate> cleanseTemplateMap;

    private final FileService fileService;

    @Autowired
    @Lazy
    private IIeTaskService iIeTaskService;
    @Value("${task.ie.shardingSize}")
    private Integer ieShardingSize;

    @Value("${task.cleanse.shardingSize}")
    private Integer cleanseShardingSize;

    /**
     * 任务执行
     * @param ieTask
     */
    @Async("taskExecutor")
    public void execute(McIeTask ieTask){
        if (ieTask.getExecutionTime()==null||ieTask.getExecutionTime().getTime()>System.currentTimeMillis()){
            log.info("任务未到执行期 :{}",ieTask.getCode());
            return;
        }

        if (!StrUtil.isBlank(ieTask.getExecutionSection())){
            boolean execution = false;
            for (String section : ieTask.getExecutionSection().split(Constants.COMMA_SPLITTER)) {
                String[] split = section.split(Constants.TRANSVERSE_LINE);
                Assert.isFalse(split.length!=2,()->new ServiceException("任务 可执行区间设置不规范 无法执行！"+ieTask.getTemplateCode()));

                boolean timeSection = TaskUtils.chackTimeSection(split[0], split[1]);
                execution = timeSection?timeSection:execution;
            }

            if (!execution){
                log.info("任务未到可执行区间内 :{}",ieTask.getCode());
                return;
            }
        }
        
        log.info("计划开始执行任务：{}",ieTask.getCode());
        TaskRequestContext context = new TaskRequestContext();
        context.setIIeTaskService(iIeTaskService);
        context.setQueryConditionJson(ieTask.getQueryConditionJson());
        context.setUserId(ieTask.getServiceModelUserId());
        context.setTaskCode(ieTask.getCode());
        context.setTemplateCode(ieTask.getTemplateCode());
        context.setState(ieTask.getState());
        context.setFileName(ieTask.getFileName());
        context.setCurrentPage(ieTask.getCurrentPage());
        switch (ieTask.getType()){
            case IMPORT:
                threadPoolExecutor.execute(new ImportExecutor(context));
                break;
            case EXPORT:
                context.setShardingSize(ieShardingSize);
                threadPoolExecutor.execute(new ExportExecutor(exportTemplateMap,fileService,context));
                break;
            case CLEANSE:
                context.setShardingSize(cleanseShardingSize);
                threadPoolExecutor.execute(new CleanseExecutor(cleanseTemplateMap,context));
                break;
            default:
                throw new ServiceException("当前任务类型不支持！"+ieTask.getType());
        }
    }
}
