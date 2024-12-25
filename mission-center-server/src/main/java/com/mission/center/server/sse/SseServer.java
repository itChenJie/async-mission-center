package com.mission.center.server.sse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * sse 功能提供
 */
@Slf4j
public class SseServer {
    private static AtomicInteger currentConnectTotal = new AtomicInteger(0);

    private static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建sse连接
     *
     * @param userIdKey - 用户标识（唯一）
     * @return
     */
    public static SseEmitter createConnect(String userIdKey) {
        // 超时时间设置为50s，设置前端的重试时间为15s
        SseEmitter sseEmitter = new SseEmitter(50000l);
        try {
            sseEmitter.send(SseEmitter.event()
                    .reconnectTime(15000l)
                    .data("前端重连成功"));
        } catch (IOException e) {
            log.error("前端重连异常 ==> userIdKey={}, 异常信息：", userIdKey, e.getMessage());
            e.printStackTrace();
        }
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(userIdKey));
        sseEmitter.onTimeout(timeOutCallBack(userIdKey));
        sseEmitter.onError(errorCallBack(userIdKey));
        sseEmitterMap.put(userIdKey, sseEmitter);
        int count = currentConnectTotal.incrementAndGet();
        log.info("创建sse连接成功 ==> 当前连接总数={}， messageId={}", count, userIdKey);
        return sseEmitter;
    }

    /**
     * 给指定 messageId发消息
     *
     * @param userIdKey - 用户标识（唯一）
     * @param message   - 消息文本
     */
    public static void sendMessage(String userIdKey, String message) {
        if (sseEmitterMap.containsKey(userIdKey)) {
            try {
                sseEmitterMap.get(userIdKey).send(message);
            } catch (IOException e) {
                log.error("发送消息异常 ==> userId={}, 异常信息：", userIdKey, e.getMessage());
                e.printStackTrace();
            }
        }
//        else {
//            throw new RuntimeException("连接不存在或者超时， userId=" + userIdKey);
//        }
    }

    /**
     * 给所有 用户 广播发送消息
     * @param message
     */
    public static void batchAllSendMessage(String message) {
        sseEmitterMap.forEach((userIdKey, sseEmitter) -> {
            try {
                sseEmitter.send(message, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                log.error("广播发送消息异常 ==> userIdKey={}, 异常信息：", userIdKey, e.getMessage());
                removeUserKey(userIdKey);
            }
        });
    }

    /**
     * 给指定 userIdKey 集合群发消息
     * @param userIdKeys
     * @param message
     */
    public static void batchSendMessage(List<String> userIdKeys, String message) {
        if (CollectionUtils.isEmpty(userIdKeys)) {
            return;
        }
        // 去重
        userIdKeys = userIdKeys.stream().distinct().collect(Collectors.toList());
        userIdKeys.forEach(userId -> sendMessage(userId, message));
    }


    /**
     * 给指定组群发消息（即组播，我们让 userIdKey满足我们的组命名确定即可）
     * @param groupId
     * @param message
     */
    public static void groupSendMessage(String groupId, String message) {
        if (MapUtils.isEmpty(sseEmitterMap)) {
            return;
        }

        sseEmitterMap.forEach((userIdKey, sseEmitter) -> {
            try {
                // 这里 groupId作为前缀
                if (userIdKey.startsWith(groupId)) {
                    sseEmitter.send(message, MediaType.APPLICATION_JSON);
                }
            } catch (IOException e) {
                log.error("组播发送消息异常 ==> groupId={}, 异常信息：", groupId, e.getMessage());
                removeUserKey(userIdKey);
            }
        });
    }

    /**
     * 移除 userKey
     *
     * @param userKey
     */
    public static void removeUserKey(String userKey) {
        sseEmitterMap.remove(userKey);
        currentConnectTotal.getAndDecrement();
        log.info("remove userKey={}", userKey);
    }

    /**
     * 获取所有的 userIdKey 集合
     *
     * @return
     */
    public static List<String> getUserIdKeys() {
        return new ArrayList<>(sseEmitterMap.keySet());
    }

    /**
     * 获取当前连接总数
     *
     * @return
     */
    public static int getConnectTotal() {
        return currentConnectTotal.intValue();
    }

    /**
     * 断开SSE连接时的回调
     *
     * @param userIdKey
     * @return
     */
    private static Runnable completionCallBack(String userIdKey) {
        return () -> {
            log.info("结束连接 ==> userIdKey={}", userIdKey);
            removeUserKey(userIdKey);
        };
    }

    /**
     * 连接超时时回调触发
     *
     * @param userIdKey
     * @return
     */
    private static Runnable timeOutCallBack(String userIdKey) {
        return () -> {
            log.info("连接超时 ==> userIdKey={}", userIdKey);
            removeUserKey(userIdKey);
        };
    }

    /**
     * 连接报错时回调触发。
     *
     * @param userIdKey
     * @return
     */
    private static Consumer<Throwable> errorCallBack(String userIdKey) {
        return throwable -> {
            log.error("连接异常 ==> userIdKey={}", userIdKey);
            removeUserKey(userIdKey);
        };
    }

    /**
     * 生成用户key
     * @param userId
     * @param system
     * @return
     */
    public static String userKey(String userId,String system){
        return String.format("userId:%s-system:%s",userId,system);
    }
}
