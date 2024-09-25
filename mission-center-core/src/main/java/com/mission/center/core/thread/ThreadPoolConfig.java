package com.mission.center.core.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(13);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("taskThread-");
        // 线程慢的情况下直接拒绝任务
        executor.setRejectedExecutionHandler(new CustomDiscardPolicy());
        executor.initialize();
        return executor;
    }
}
