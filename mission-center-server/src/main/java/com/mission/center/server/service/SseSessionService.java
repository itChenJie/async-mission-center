package com.mission.center.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSE 连接管理器
 * 管理当前节点的 SSE 连接，支持向本地客户端推送消息
 * Key 格式：moduleCode:userId（因为多系统共用任务中心，userId 不能保证唯一性）
 * Value：CopyOnWriteArrayList，支持同一用户在同一节点上的多终端/多标签页连接
 */
@Slf4j
@Service
public class SseSessionService {

    private static final String KEY_SEPARATOR = ":";

    private static final Map<String, CopyOnWriteArrayList<SseEmitter>> SSE_CACHE = new ConcurrentHashMap<>();

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

        // 保证线程安全地初始化 List 并添加连接
        SSE_CACHE.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // 注册回调，在断开、超时或异常时清理当前这个连接
        Runnable removeCallback = () -> {
            CopyOnWriteArrayList<SseEmitter> emitters = SSE_CACHE.get(key);
            if (emitters != null) {
                emitters.remove(emitter);
                log.info("SSE 连接移除，key: {}, 剩余连接数: {}", key, emitters.size());
                if (emitters.isEmpty()) {
                    SSE_CACHE.remove(key);
                }
            }
        };

        emitter.onCompletion(removeCallback);
        emitter.onTimeout(removeCallback);
        emitter.onError((e) -> {
            log.error("SSE 连接异常，key: {}", key, e);
            removeCallback.run();
        });

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 连接建立成功"));
            log.info("SSE 连接建立成功，key: {}, 当前连接数: {}", key, SSE_CACHE.get(key).size());
        } catch (IOException e) {
            log.error("SSE 初始化发送异常，key: {}", key, e);
            removeCallback.run();
        }
        return emitter;
    }

    /**
     * 发送消息给本地的 SSE 连接 (群发给该用户的所有标签页/设备)
     */
    public void sendToLocalClient(String moduleCode, String userId, String message) {
        String key = buildKey(moduleCode, userId);
        CopyOnWriteArrayList<SseEmitter> emitters = SSE_CACHE.get(key);
        if (emitters != null && !emitters.isEmpty()) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("task-notice").data(message));
                    log.info("SSE 消息推送成功，key: {}, message: {}", key, message);
                } catch (IOException e) {
                    log.error("SSE 推送消息异常，key: {}", key, e);
                    emitters.remove(emitter);
                }
            }
            if (emitters.isEmpty()) {
                SSE_CACHE.remove(key);
            }
        }
    }

    /**
     * 关闭指定用户的所有 SSE 连接
     */
    public void disconnect(String moduleCode, String userId) {
        String key = buildKey(moduleCode, userId);
        CopyOnWriteArrayList<SseEmitter> emitters = SSE_CACHE.remove(key);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                emitter.complete();
            }
            log.info("关闭所有 SSE 连接，key: {}, 连接数: {}", key, emitters.size());
        }
    }
}