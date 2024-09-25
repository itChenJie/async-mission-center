package com.mission.center.util;

import cn.hutool.core.util.StrUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskUtils {

    /**
     *  校验当前小时 是否在可执行区间内
     * @param start
     * @param end
     * @return
     */
    public static boolean chackTimeSection(String start,String end){
        if (StrUtil.isBlank(start)||StrUtil.isBlank(end))
            return false;

        LocalTime morningStart = LocalTime.of(Integer.parseInt(start), 0);
        LocalTime eveningEnd = LocalTime.of(Integer.parseInt(end), 0);
        LocalTime currentTime = LocalDateTime.now().toLocalTime();
        return currentTime.isAfter(morningStart) && currentTime.isBefore(eveningEnd);
    }
}
