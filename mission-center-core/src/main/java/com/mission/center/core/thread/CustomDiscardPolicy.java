package com.mission.center.core.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池拒绝策略
 * @Description
 **/
@Slf4j
public class CustomDiscardPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.warn("导入、导出线程池已满，线程池大小：{},当前任务暂时抛弃", executor.getMaximumPoolSize());
    }
}
