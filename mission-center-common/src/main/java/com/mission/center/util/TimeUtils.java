package com.mission.center.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mission.center.constants.TimeUnit;
import com.mission.center.error.ServerCode;
import com.mission.center.error.ServiceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 检查时间间隔 是否符合要求
     * @param isTimeInterval 标识是否有值
     * @param startTimeStr 开始时间
     * @param endTimeStr 结束时间
     * @param intervalType 间隔类型
     * @param interval 间隔
     * @return
     */
    public static boolean checkIsTimeInterval(boolean isTimeInterval, String startTimeStr, String endTimeStr, TimeUnit intervalType, long interval) {
        if (StringUtils.isNotBlank(startTimeStr) && StringUtils.isNotBlank(endTimeStr)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr,formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr,formatter);
            long timeDifference = calculateTimeDifference(intervalType, startTime, endTime);
            if (timeDifference > interval){
                throw new ServiceException(String.format("筛选时间 跨度不能超过 %s %s", interval, intervalType.getName()));
            }
            isTimeInterval = true;
        }
        return isTimeInterval;
    }
    public static long calculateTimeDifference(TimeUnit unit, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)){
            throw new ServiceException("开始时间 不能大于结束时间");
        }
        switch (unit) {
            case DAY:
                return ChronoUnit.DAYS.between(startTime, endTime);
            case WEEKS:
                return ChronoUnit.WEEKS.between(startTime, endTime);
            case MONTH:
                return ChronoUnit.MONTHS.between(startTime, endTime)+1;
            case YEARS:
                return ChronoUnit.YEARS.between(startTime, endTime);
            default:
                throw new IllegalArgumentException("Unsupported TimeUnit: " + unit);
        }
    }
}
