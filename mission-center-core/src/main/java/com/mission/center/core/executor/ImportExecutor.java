package com.mission.center.core.executor;


import com.mission.center.core.bean.TaskRequestContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 导入执行器
 **/
@Slf4j
public class ImportExecutor extends Thread{
    private TaskRequestContext context;
    public ImportExecutor(TaskRequestContext context){
        this.context = context;
    }

    @Override
    public void run() {

    }
}
