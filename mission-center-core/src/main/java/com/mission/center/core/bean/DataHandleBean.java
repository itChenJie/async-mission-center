package com.mission.center.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataHandleBean {
    /**
     * 成功数
     */
    private Long successNumber = 0l;
    /**
     * 失败数
     */
    private Long failNumber= 0l;
    /**
     * 当前执行分页数据下标
     */
    private int index;
    /**
     * 下次执行时间
     */
    private Date nextExecutionTime;
    public void addSuccessNumber(Long successNumber){
        this.successNumber += successNumber;
    }

    public void addFailNumber(Long failNumber){
        this.failNumber += failNumber;
    }
}
