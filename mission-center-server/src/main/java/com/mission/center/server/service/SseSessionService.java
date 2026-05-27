package com.mission.center.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 连接管理器
 * 管理当前节点的 SSE 连接，支持向本地客户端推送消息
 * Key 格式：moduleCode:userId（因为多系统共用任务中心，userId 不能保证唯一性）
 */
@Slf4j
@Service
public class SseSessionService {

    private static final String KEY_SEPARATOR = ":";

    private static final Map<String, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();

    /**
     * 生成唯一标识 key
     */
    private String buildKey(String moduleCode, String userId) {
        return moduleCode + KEY_SEPARATOR + userId;
    }

    /**
     * 建立 SSE 连接
     */
    public SseEmitter connect(String moduleCode, String userId) {
        String key = buildKey(moduleCode, userId);
        SseEmitter emitter = new SseEmitter(0L);
        SSE_CACHE.put(key, emitter);

        emitter.onCompletion(() -> {
            log.info("SSE 连接完成，移除 key: {}", key);
            SSE_CACHE.remove(key);
        });
        emitter.onTimeout(() -> {
            log.info("SSE 连接超时，移除 key: {}", key);
            SSE_CACHE.remove(key);
        });
        emitter.onError((e) -> {
            log.error("SSE 连接异常，移除 key: {}", key, e);
            SSE_CACHE.remove(key);
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 连接建立成功"));
        } catch (IOException e) {
            log.error("SSE 连接异常，key: {}", key, e);
            SSE_CACHE.remove(key);
        }
        return emitter;
    }

    /**
     * 发送消息给本地的 SSE 连接
     */
    public void sendToLocalClient(String moduleCode, String userId, String message) {
        String key = buildKey(moduleCode, userId);
        SseEmitter emitter = SSE_CACHE.get(key);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("task-notice").data(message));
                log.info("SSE 消息发送成功，key: {}, message: {}", key, message);
            } catch (IOException e) {
                log.error("SSE 消息发送失败，key: {}", key, e);
                SSE_CACHE.remove(key);
            }
        }
    }

    /**
     * 关闭指定用户的 SSE 连接
     */
    public void disconnect(String moduleCode, String userId) {
        String key = buildKey(moduleCode, userId);
        SseEmitter emitter = SSE_CACHE.remove(key);
        if (emitter != null) {
            emitter.complete();
        }
    }
}