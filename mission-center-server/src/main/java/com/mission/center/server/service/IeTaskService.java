package com.mission.center.server.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constant.IeTaskType;
import com.mission.center.constants.Constants;
import com.mission.center.constants.RedisKey;
import com.mission.center.core.bean.UpdateNumberBean;
import com.mission.center.core.bean.UpdateStateBean;
import com.mission.center.core.file.OSSClientUtil;
import com.mission.center.core.task.IIeTaskService;
import com.mission.center.core.template.TaskTemplate;
import com.mission.center.entity.ResponseWrapper;
import com.mission.center.error.ServiceException;
import com.mission.center.server.convert.McIeTaskConvert;
import com.mission.center.server.entity.McIeTask;
import com.mission.center.server.executor.AsyncTaskService;
import com.mission.center.server.mapper.McIeTaskMapper;
import com.mission.center.server.strategy.user.UserStrategy;
import com.mission.center.server.util.CodeUtilBean;
import com.mission.center.server.vo.*;
import com.mission.center.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 导出导入任务中心
 * @Author: chenWenJie
 */
@Service
@RequiredArgsConstructor
public class IeTaskService implements IIeTaskService {
    private final McIeTaskMapper mcIeTaskMapper;

    private final McIeTaskConvert mcIeTaskConvert;

    private final CodeUtilBean codeUtilBean;

    private final Map<String, TaskTemplate> exportTemplateMap;

    private final AsyncTaskService asyncTaskService;

    private final Map<String, UserStrategy> userStrategyMap;

    private final StringRedisTemplate redisTemplate;

    private final OSSClientUtil ossClientUtil;

    @Autowired
    @Lazy
    private IIeTaskService iIeTaskService;

    /**
     * 创建任务
     * @param request
     * @return
     */
    public ResponseWrapper<IeTaskListBean> addTask(IeTaskAddRequest request) {
        McIeTask ieTask = mcIeTaskConvert.toIeTask(request);
        TaskTemplate iExportTemplate = exportTemplateMap.get(request.getTemplateCode());
        checkAddTask(iExportTemplate,request);

        ieTask.setCode(codeUtilBean.getIeTaskCode(iExportTemplate.taskType().getValue()));
        ieTask.setTemplateName(iExportTemplate.name());
        ieTask.setModuleCode(iExportTemplate.moduleCode());
        ieTask.setType(iExportTemplate.taskType());
        if (!StringUtils.isBlank(request.getDetailsUrl())) {
            ieTask.setFileKey(request.getDetailsUrl() + Constants.SLASH_SEPARATOR + ieTask.getCode());
        }
        if (!request.isDelayExecution()){
            ieTask.setExecutionTime(new Date());
        }else {
            ieTask.setExecutionTime(iExportTemplate.executionTime(true));
        }
        ieTask.setExecutionSection(iExportTemplate.executionSection());
        ieTask.setState(IeTaskState.WAIT_START);
        ieTask.setCurrentPage(0);
        mcIeTaskMapper.insert(ieTask);
        asyncTaskService.execute(ieTask);
        IeTaskListBean taskListBean = new IeTaskListBean();
        BeanUtils.copyProperties(ieTask,taskListBean);
        return ResponseWrapper.success(taskListBean);
    }

    /**
     * 检查创建任务 必要参数
     * @param request
     */
    private void checkAddTask(TaskTemplate iExportTemplate,IeTaskAddRequest request) {
        Assert.notNull(iExportTemplate,() ->new ServiceException("模版不存在"));

        if (iExportTemplate.taskType()== IeTaskType.IMPORT){
            Assert.isFalse(StringUtils.isBlank(request.getFileName())||StringUtils.isBlank(request.getImportFileKey())
                    ,()-> new ServiceException("导入文件名、文件地址不能为空！"));
            Assert.isFalse(StringUtils.isBlank(request.getDetailsUrl()),()-> new ServiceException("数据详情列表页面路由地址不能为空！"));
        }else if(iExportTemplate.taskType()== IeTaskType.CLEANSE){
            Assert.isFalse(StringUtils.isBlank(request.getDetailsUrl()),()-> new ServiceException("数据详情列表页面路由地址不能为空！"));
        }
    }

    /**
     * 任务列表查询
     * @param request
     * @return
     */
    public IeTaskListResponse list(IeTaskListRequest request) {
        IeTaskListResponse response = new IeTaskListResponse();

        Page<McIeTask> page = PageDTO.of(request.getPage(),request.getPageSize());
        LambdaUpdateWrapper<McIeTask> wrapper = new LambdaUpdateWrapper<McIeTask>()
                .eq(!StringUtils.isBlank(request.getCode()), McIeTask::getCode, request.getCode())
                .eq(!ObjectUtil.isNull(request.getModuleCode()), McIeTask::getModuleCode, request.getModuleCode())
                .eq(!StringUtils.isBlank(request.getMenuCode()), McIeTask::getMenuCode, request.getMenuCode())
                .eq(!StringUtils.isBlank(request.getTemplateCode()), McIeTask::getTemplateCode, request.getTemplateCode())
                .in(!ObjectUtil.isNull(request.getTypes()), McIeTask::getType, request.getTypes())
                .eq(!StringUtils.isBlank(request.getServiceModelUserId()), McIeTask::getServiceModelUserId, request.getServiceModelUserId())
                .eq(!StringUtils.isBlank(request.getChannelCode()), McIeTask::getChannelCode, request.getChannelCode())
                .between(!StringUtils.isBlank(request.getStartTime()) && !StringUtils.isBlank(request.getEndTime()), McIeTask::getCreateTime
                        , request.getStartTime(), request.getEndTime())
                .like(!StringUtils.isBlank(request.getFileName()), McIeTask::getFileName, request.getFileName())
                .orderByDesc(McIeTask::getCreateTime);
        if (request.getState()!=null){
            if (request.getState()==IeTaskState.EXECUTING){
                wrapper.in(McIeTask::getState, IeTaskState.EXECUTING,IeTaskState.FILE_UPLOADING);
            }else {
                wrapper.eq( McIeTask::getState, request.getState());
            }
        }
        Page<McIeTask> taskPage = mcIeTaskMapper.selectPage(page, wrapper);
        if (ObjectUtil.isNull(taskPage.getRecords()))
            return response;

        response.setTaskList(BeanCopyUtils.copyBeanList(taskPage.getRecords(), IeTaskListBean.class));
        List<String> userIds = response.getTaskList().stream()
                .map(item -> item.getServiceModelUserId())
                .distinct().collect(Collectors.toList());
        UserStrategy strategy = userStrategyMap.get(request.getModuleCode().getValue() +Constants.USER_STRATEGY);
        Map<String, String> userMap = new HashMap<>();
        if (strategy!=null){
            userMap = strategy.userMap(userIds);
        }
        for (IeTaskListBean taskListBean : response.getTaskList()) {
            taskListBean.setCreateName(userMap.get(taskListBean.getServiceModelUserId()));
            // 进度变化要求高的模版，模版内部每处理一条数据，缓存更新进度
            if (taskListBean.getState() ==IeTaskState.EXECUTING){
                String schedule = redisTemplate.opsForValue().get(RedisKey.TASK_SCHEDULE + taskListBean.getCode());
                taskListBean.setSchedule(!StringUtils.isBlank(schedule)? Integer.valueOf(schedule) :taskListBean.getSchedule());
            }
            if (taskListBean.getState()==IeTaskState.SUCCESS
                    &&taskListBean.getType()!=IeTaskType.CLEANSE
                    &&!StringUtils.isBlank(taskListBean.getFileKey())){
                taskListBean.setFileKey(ossClientUtil.getUrl(taskListBean.getFileKey()));
            }
        }
        response.setTotal(page.getTotal());
        return response;
    }

    /**
     * 更新任务状态
     *
     * @param code
     * @param stateBean
     * @return
     */
    @Override
    public boolean updateState(String code, UpdateStateBean stateBean) {
        McIeTask ieTask = new McIeTask();
        ieTask.setState(stateBean.getNewState());
        if(stateBean.getDesc()!=null) {
            ieTask.setDescription(stateBean.getDesc());
        }
        if (stateBean.getNewState()==IeTaskState.SUCCESS){
            ieTask.setCompleteTime(new Date());
        }
        int update = mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code)
                .eq(stateBean.getOldState() != null, McIeTask::getState, stateBean.getOldState()));
        return update > 0;
    }

    /**
     * 更新任务进度
     * @param code     任务编码
     * @param schedule 进度
     * @param currentPage
     * @param currentPageInIndex 当前分页内下标
     * @return
     */
    @Override
    public boolean updateSchedule(String code, Integer schedule,Integer currentPage,Integer currentPageInIndex) {
        McIeTask ieTask = new McIeTask();
        ieTask.setSchedule(schedule);
        ieTask.setCurrentPage(currentPage);
        ieTask.setCurrentPageInIndex(currentPageInIndex);
        int update = mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code));
        return update > 0;
    }

    /**
     * 更新任务文件远程地址
     * @param ossKey
     * @param code
     */
    @Override
    public void updateFileRemoteAddress(String ossKey, String code) {
        McIeTask ieTask = new McIeTask();
        ieTask.setFileKey(ossKey);
        mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code));
    }

    /**
     * 更新文件名称
     * @param code
     * @param fileName
     */
    @Override
    public void updateFileName(String code, String fileName) {
        McIeTask ieTask = new McIeTask();
        ieTask.setFileName(fileName);
        mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code));
    }

    /**
     * 更新处理数据条数
     *
     * @param code
     * @param numberBean
     */
    @Override
    public void updateNumber(String code, UpdateNumberBean numberBean) {
        McIeTask ieTask = new McIeTask();
        if (numberBean.getTotalNumber()!=null){
            ieTask.setTotalNumber(numberBean.getTotalNumber());
        }
        if (numberBean.getSuccessNumber()!=null){
            ieTask.setSuccessNumber(numberBean.getSuccessNumber());
        }
        if (numberBean.getFailNumber()!=null){
            ieTask.setFailNumber(numberBean.getFailNumber());
        }
        mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code));
    }

    /**
     * 开启任务 更新执行时间
     * 如果执行时间 已有值表示已经开启
     * @param request
     * @return
     */
    public void startTask(IeTaskRequest request) {
        McIeTask mcIeTask = mcIeTaskMapper.selectOne(new LambdaQueryWrapper<McIeTask>().eq(McIeTask::getCode, request.getTaskCode()));
        Assert.isFalse(mcIeTask ==null,()-> new ServiceException("任务编码不存在！"));

        Assert.isFalse(!ObjectUtil.isNull(mcIeTask.getExecutionTime()),()->new ServiceException("任务已启动！"));
        TaskTemplate iExportTemplate = exportTemplateMap.get(mcIeTask.getTemplateCode());
        mcIeTask.setExecutionTime(iExportTemplate.executionTime(false));
        mcIeTaskMapper.updateById(mcIeTask);
        asyncTaskService.execute(mcIeTask);
    }

    public IeTaskListBean quantity(IeTaskRequest request) {
        McIeTask mcIeTask = mcIeTaskMapper.selectOne(new LambdaQueryWrapper<McIeTask>().eq(McIeTask::getCode, request.getTaskCode()));
        Assert.isFalse(mcIeTask ==null,()-> new ServiceException("任务编码不存在！"));

        IeTaskListBean taskListBean = new IeTaskListBean();
        BeanUtils.copyProperties(mcIeTask,taskListBean);
        taskListBean.setTotalNumber(mcIeTask.getTotalNumber());
        taskListBean.setSuccessNumber(mcIeTask.getSuccessNumber());
        taskListBean.setFailNumber(mcIeTask.getFailNumber());
        return taskListBean;
    }

    /**
     * 任务执行
     * @param code
     */
    public void asyncTaskExecute(String code) {
        McIeTask mcIeTask = mcIeTaskMapper.selectOne(new LambdaQueryWrapper<McIeTask>().eq(McIeTask::getCode, code));
        asyncTaskService.execute(mcIeTask);
    }

    /**
     * 保存临时文件存储 服务器ip
     * @param code
     * @param ip
     */
    @Override
    public void updateTempFileSaveIp(String code, String ip) {
        McIeTask ieTask = new McIeTask();
        ieTask.setTempFileSaveIp(ip);
        mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code));
    }

    /**
     * 任务挂起
     * @param code
     * @param nextExecutionTime
     */
    @Override
    public void hangUpTask(String code, Date nextExecutionTime) {
        McIeTask ieTask = new McIeTask();
        ieTask.setState(IeTaskState.STOP);
        ieTask.setExecutionTime(nextExecutionTime);
        mcIeTaskMapper.update(ieTask, new LambdaUpdateWrapper<McIeTask>()
                .eq(McIeTask::getCode, code));
    }
}
