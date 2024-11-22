package com.mission.center.core.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:application.yml")
public class ThreadPoolConfig {
    @Value("${ie.taskThreadPool.core}")
    private int core;
    @Value("${ie.taskThreadPool.max}")
    private int max;
    @Value("${ie.taskThreadPool.time}")
    private int time;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolExecutor threadPoolTaskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(core, max, time, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), (r, e) -> {
            throw new RejectedExecutionException("导入、导出线程池已满，线程池大小：{},当前任务暂时抛弃: " + e.getMaximumPoolSize());
        });
        return executor;
    }
}
