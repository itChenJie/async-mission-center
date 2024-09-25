package com.mission.center.core.executor;

import cn.hutool.core.lang.Assert;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constants.Constants;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.bean.UpdateStateBean;
import com.mission.center.core.file.FileService;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.core.template.impl.AbstractExportTemplate;
import com.mission.center.error.ServiceException;
import com.mission.center.excel.FileSuffixEnum;
import com.mission.center.util.IpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

/**
 * @Description 导出任务执行器
 **/
@Slf4j
public class ExportExecutor extends Thread {

    private Map<String, AbstractExportTemplate>  exportTemplateMap;

    private FileService fileService;

    private TaskRequestContext context;

    public ExportExecutor(Map<String, AbstractExportTemplate> exportTemplateMap, FileService fileService
            ,TaskRequestContext context) {
        this.exportTemplateMap = exportTemplateMap;
        this.fileService = fileService;
        this.context = context;
    }

    @Override
    public void run() {
        log.info("导出任务开始执行：{}  state:{}",context.getTaskCode(),context.getState().getValue());
        IIeTaskService iIeTaskService = context.getIIeTaskService();

        // 文件上传oss
        if (context.getState().equals(IeTaskState.FILE_UPLOADING)) {
            Assert.isFalse(StringUtils.isBlank(context.getFileName()),()->new ServiceException("文件名不能为空！"));
            File file = new File(fileService.getRootPath()+ Constants.SLASH_SEPARATOR+context.getFileName());
            if (!file.exists()){
                log.error("文件不存在当前服务器！{}  {}",context.getFileName(),context.getTaskCode());
                iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.FILE_UPLOADING, null, "文件不存在当前服务器"));
                return;
            }
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.FILE_UPLOADING, null, ""));
            String ossKey = fileService.upload(context.getFileName());
            iIeTaskService.updateFileRemoteAddress(ossKey,context.getTaskCode());
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.SUCCESS));
            fileService.deleteTempFile(new File(fileService.getRootPath()
                    + Constants.SLASH_SEPARATOR+context.getFileName()).getPath());
            return;
        }

        boolean updateState = iIeTaskService.updateState(context.getTaskCode(),new UpdateStateBean(IeTaskState.EXECUTING, IeTaskState.WAIT_START));
        Assert.isFalse(!updateState,() ->new ServiceException("当前任务不是待启动无法执行导出："+context.getTaskCode()));

        // 获取导出模版
        AbstractExportTemplate downloadTemplate = exportTemplateMap.get(context.getTemplateCode());

        if (downloadTemplate==null){
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.EXECUTING,null,"当前导出模版暂不支持！"));
            return;
        }
        try {
            iIeTaskService.updateTempFileSaveIp(context.getTaskCode(), IpUtils.localIP());
            //创建文件，如果文件已经存在 删除文件再创建
            String fileName = downloadTemplate.name()+Constants.UNDERLINE+context.getTaskCode()
                    + Constants.FILE_SUFFIX_SEPARATOR+ FileSuffixEnum.EXCEL_07.getCode();
            String filePath = fileService.getRootPath()+ Constants.SLASH_SEPARATOR+fileName;
            fileService.createOrReplaceFile(filePath);
            File file = new File(filePath);
            iIeTaskService.updateFileName(context.getTaskCode(),fileName);
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
                context.setOut(out);
                boolean execute = downloadTemplate.execute(context);
                if (execute){
                    String ossKey = fileService.upload(fileName);
                    iIeTaskService.updateFileRemoteAddress(ossKey, context.getTaskCode());
                    iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.SUCCESS));
                    fileService.deleteTempFile(file.getPath());
                }
            }catch (Exception e){
                log.error("数据导出异常",e);
                iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.FAIL, null, e.getMessage()));
            }
        }catch (Exception e){
            log.error("数据导出 文件处理异常",e);
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.FAIL, null, e.getMessage()));
        }

    }
}
