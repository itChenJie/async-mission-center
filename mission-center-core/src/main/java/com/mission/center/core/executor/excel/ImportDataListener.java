package com.mission.center.core.executor.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.mission.center.core.bean.DataHandleBean;
import com.mission.center.core.bean.UpdateNumberBean;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.core.template.impl.AbstractImportTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入数据监听器
 * 使用 EasyExcel 流式读取，分批处理数据
 */
@Slf4j
public class ImportDataListener<T> implements ReadListener<T> {
    private List<T> cachedDataList;

    private final AbstractImportTemplate<T> importTemplate;
    private final String taskCode;
    private final IIeTaskService taskService;

    private long totalSuccess = 0;
    private long totalFail = 0;
    private Integer shardingSize =0;

    public ImportDataListener(AbstractImportTemplate<T> importTemplate, String taskCode, IIeTaskService taskService, int shardingSize) {
        this.importTemplate = importTemplate;
        this.taskCode = taskCode;
        this.taskService = taskService;
        this.shardingSize = shardingSize;
        cachedDataList = new ArrayList<>(shardingSize);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= shardingSize) {
            processData();
            cachedDataList = new ArrayList<>(shardingSize);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cachedDataList.isEmpty()) {
            processData();
        }
        log.info("导入任务完成，taskCode: {}, 成功: {}, 失败: {}", taskCode, totalSuccess, totalFail);
    }

    private void processData() {
        try {
            DataHandleBean handleBean = importTemplate.importData(taskCode, cachedDataList);

            totalSuccess += (handleBean.getSuccessNumber() != null ? handleBean.getSuccessNumber() : 0);
            totalFail += (handleBean.getFailNumber() != null ? handleBean.getFailNumber() : 0);

            taskService.updateNumber(taskCode, new UpdateNumberBean(null, totalSuccess, totalFail));
        } catch (Exception e) {
            log.error("导入数据处理异常, taskCode: {}", taskCode, e);
            totalFail += cachedDataList.size();
            taskService.updateNumber(taskCode, new UpdateNumberBean(null, totalSuccess, totalFail));
        }
    }
}