package com.mission.center.server.controller;

import com.mission.center.entity.ResponseWrapper;
import com.mission.center.server.sse.SseServer;
import com.mission.center.server.vo.SeeConnectRequest;
import com.mission.center.server.vo.SeePushAllRequest;
import com.mission.center.server.vo.SendMessageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
@Api(description = "站内通信")
public class SseController {

    @ApiOperation("创建SSE连接")
    @GetMapping("/createConnect")
    public ResponseWrapper<SseEmitter> createConnect(@RequestBody @Valid SeeConnectRequest request) {
        return ResponseWrapper.success(SseServer.createConnect(SseServer.userKey(request.getUserId(),request.getSystem())));
    }

    @ApiOperation("发送消息")
    @PostMapping("/sendMessage")
    public ResponseWrapper sendMessage(@RequestBody @Valid SendMessageRequest request){
        SseServer.sendMessage(SseServer.userKey(request.getUserId(),request.getSystem()),request.getMessage());
        return new ResponseWrapper();
    }

    @ApiOperation("广播发送")
    @GetMapping("/pushAllUser")
    public ResponseWrapper pushAllUser(@RequestBody @Valid SeePushAllRequest request){
        SseServer.batchAllSendMessage(request.getMessage());
        return new ResponseWrapper();
    }

    @ApiOperation("下线")
    @PostMapping("/offline")
    public ResponseWrapper offline(@RequestParam String userId,@RequestParam String system){
        SseServer.removeUserKey(SseServer.userKey(userId,system));
        return new ResponseWrapper();
    }

    @ApiOperation("总连接数")
    @GetMapping("/totalConnect")
    public ResponseWrapper<Integer> totalConnect(){
        return ResponseWrapper.success(SseServer.getConnectTotal());
    }

    @ApiOperation("获取当前连接用户")
    @GetMapping("/getConnectUser")
    public ResponseWrapper<List<String>> getConnectUser(){
        return ResponseWrapper.success(SseServer.getUserIdKeys());
    }
}
