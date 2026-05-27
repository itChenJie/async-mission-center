package com.mission.center.server.controller;

import com.mission.center.server.service.SseSessionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("sse")
@RequiredArgsConstructor
@Api(tags = "站内信(SSE)模块")
public class SseController {

    private final SseSessionService sseSessionService;

    @ApiOperation("建立SSE连接")
    @GetMapping(value = "connect/{moduleCode}/{userId}", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter connect(@PathVariable("moduleCode") String moduleCode,
                              @PathVariable("userId") String userId) {
        return sseSessionService.connect(moduleCode, userId);
    }

    @ApiOperation("断开SSE连接")
    @GetMapping("disconnect/{moduleCode}/{userId}")
    public void disconnect(@PathVariable("moduleCode") String moduleCode,
                           @PathVariable("userId") String userId) {
        sseSessionService.disconnect(moduleCode, userId);
    }
}