package com.mission.center.core.executor;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcel;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constants.Constants;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.bean.UpdateStateBean;
import com.mission.center.core.executor.excel.ImportDataListener;
import com.mission.center.core.file.FileService;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.core.template.impl.AbstractImportTemplate;
import com.mission.center.error.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * 导入任务执行器
 */
@Slf4j
public class ImportExecutor extends Thread {

    private final Map<String, AbstractImportTemplate> importTemplateMap;
    private final FileService fileService;
    private final TaskRequestContext context;

    public ImportExecutor(Map<String, AbstractImportTemplate> importTemplateMap,
                          FileService fileService, TaskRequestContext context) {
        this.importTemplateMap = importTemplateMap;
        this.fileService = fileService;
        this.context = context;
    }

    @Override
    public void run() {
        log.info("导入任务开始执行：{}  state:{}", context.getTaskCode(), context.getState().getValue());
        IIeTaskService iIeTaskService = context.getIIeTaskService();

        // 1. 锁定状态，更新为执行中
        boolean updateState = iIeTaskService.updateState(context.getTaskCode(),
                new UpdateStateBean(IeTaskState.EXECUTING, IeTaskState.WAIT_START));
        Assert.isFalse(!updateState, () -> new ServiceException("当前任务不是待启动，无法执行导入：" + context.getTaskCode()));

        // 2. 获取导入模板
        AbstractImportTemplate template = importTemplateMap.get(context.getTemplateCode());
        if (template == null) {
            iIeTaskService.updateState(context.getTaskCode(),
                    new UpdateStateBean(IeTaskState.FAIL, null, "当前导入模版暂不支持！"));
            return;
        }

        String localFilePath = null;
        try {
            // 3. 将OSS文件下载到本地临时目录
            String fileName = context.getFileName();
            localFilePath = fileService.getRootPath() + Constants.SLASH_SEPARATOR
                    + context.getTaskCode() + "_" + fileName;
            fileService.downloadFromOss(context.getImportFileKey(), localFilePath);

            // 4. 使用 EasyExcel 解析
            File localFile = new File(localFilePath);
            Assert.isFalse(!localFile.exists(), () -> new ServiceException("OSS文件下载到本地失败"));

            ImportDataListener listener = new ImportDataListener<>(template, context.getTaskCode(), iIeTaskService,context.getShardingSize());
            EasyExcel.read(localFile, template.getExcelClass(), listener).sheet().doRead();

            // 5. 导入完成，更新状态为成功
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.SUCCESS));

        } catch (Exception e) {
            log.error("数据导入异常", e);
            iIeTaskService.updateState(context.getTaskCode(),
                    new UpdateStateBean(IeTaskState.FAIL, null, e.getMessage()));
        } finally {
            // 6. 清理本地临时文件
            if (localFilePath != null) {
                fileService.deleteTempFile(localFilePath);
            }
        }
    }
}