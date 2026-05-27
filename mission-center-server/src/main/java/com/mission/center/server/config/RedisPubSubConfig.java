package com.mission.center.server.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mission.center.server.service.SseSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis Pub/Sub 配置
 * 监听 SSE 消息频道，实现跨节点消息推送
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisPubSubConfig {

    public static final String SSE_TASK_CHANNEL = "sse:task:notify:channel";

    private final SseSessionService sseSessionService;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String body = new String(message.getBody());
                    JSONObject data = JSON.parseObject(body);
                    String moduleCode = data.getString("moduleCode");
                    String userId = data.getString("userId");
                    String msg = data.getString("msg");

                    log.info("收到 Redis SSE 广播消息，moduleCode: {}, userId: {}, msg: {}", moduleCode, userId, msg);
                    sseSessionService.sendToLocalClient(moduleCode, userId, msg);
                } catch (Exception e) {
                    log.error("解析 Redis SSE 广播消息失败", e);
                }
            }
        }, new ChannelTopic(SSE_TASK_CHANNEL));

        return container;
    }
}