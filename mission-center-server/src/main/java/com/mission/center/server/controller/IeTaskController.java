package com.mission.center.server.controller;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.mission.center.constants.RedisKey;
import com.mission.center.entity.ResponseWrapper;
import com.mission.center.error.ServiceException;
import com.mission.center.server.service.IeTaskService;
import com.mission.center.server.service.UserService;
import com.mission.center.server.vo.*;
import com.mission.center.util.IdempotencyHandlerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @Author: chenWenJie
 */

@RestController
@RequestMapping("task")
@RequiredArgsConstructor
@Api(description = "导入导出任务中心")
public class IeTaskController {
    private final IeTaskService taskService;

    private final UserService userService;

    private final StringRedisTemplate redisTemplate;

    @ApiOperation("导出导入任务查询")
    @PostMapping("list")
    public ResponseWrapper<IeTaskListResponse> companyCityList(@RequestBody @Valid IeTaskListRequest request) {
        IeTaskListResponse response= taskService.list(request);
        return ResponseWrapper.success(response);
    }

    @ApiOperation("导出、导入生成任务")
    @PostMapping("add")
    public ResponseWrapper<IeTaskListBean> addTask(@RequestBody @Valid IeTaskAddRequest request) {
        // TODO 改成AOP
        String requestId = IdempotencyHandlerUtil.generateRequestId(JSON.toJSONString(request));
        Assert.isFalse(redisTemplate.hasKey(RedisKey.TASK_REQUEST_ID + requestId),()->new ServiceException("操作过于频繁，请稍后！"));
        redisTemplate.opsForValue().set(RedisKey.TASK_REQUEST_ID + requestId,requestId,30, TimeUnit.SECONDS);
        ResponseWrapper<IeTaskListBean> response = taskService.addTask(request);
        return response;
    }

    @ApiOperation("查询当前对应系统下用户")
    @PostMapping("findUserList")
    public ResponseWrapper<IeTaskUserListResponse> findUserList(@RequestBody @Valid IeTaskUserListRequest request){
        IeTaskUserListResponse response= userService.findUserList(request);
        return ResponseWrapper.success(response);
    }

    @ApiOperation("开启任务")
    @PostMapping("startTask")
    public ResponseWrapper startTask(@RequestBody @Valid IeTaskRequest request) {
        taskService.startTask(request);
        return new ResponseWrapper();
    }

    @ApiOperation("任务成功 失败 总数 数量")
    @PostMapping("quantity")
    public ResponseWrapper<IeTaskListBean> quantity(@RequestBody @Valid IeTaskRequest request) {
        IeTaskListBean bean= taskService.quantity(request);
        return ResponseWrapper.success(bean);
    }

    @ApiOperation("任务异步执行")
    @PostMapping("asyncTaskExecute")
    public ResponseWrapper asyncTaskExecute(@RequestParam(value = "code") String code){
        taskService.asyncTaskExecute(code);
        return new ResponseWrapper();
    }
}
