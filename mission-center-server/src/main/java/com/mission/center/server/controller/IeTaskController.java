package com.mission.center.server.controller;

import com.mission.center.annotation.Idempotent;
import com.mission.center.constants.RedisKey;
import com.mission.center.entity.ResponseWrapper;
import com.mission.center.server.service.IeTaskService;
import com.mission.center.server.service.UserService;
import com.mission.center.server.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("task")
@RequiredArgsConstructor
@Api(tags = "导入导出任务中心")
public class IeTaskController {
    private final IeTaskService taskService;

    private final UserService userService;

    @ApiOperation("导出导入任务查询")
    @PostMapping("list")
    public ResponseWrapper<IeTaskListResponse> companyCityList(@RequestBody @Valid IeTaskListRequest request) {
        IeTaskListResponse response= taskService.list(request);
        return ResponseWrapper.success(response);
    }

    @ApiOperation("导出、导入生成任务")
    @PostMapping("add")
    @Idempotent(prefix = RedisKey.TASK_REQUEST_ID)
    public ResponseWrapper<IeTaskListBean> addTask(@RequestBody @Valid IeTaskAddRequest request) {
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
