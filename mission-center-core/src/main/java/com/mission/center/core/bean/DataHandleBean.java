package com.mission.center.core.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
