package com.mission.center.annotation;

import cn.hutool.core.lang.Assert;
import com.mission.center.constants.TimeIntervalFieldEnum;
import com.mission.center.error.ServiceException;
import com.mission.center.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制层 筛选时间跨度校验
 */
@Component
@Aspect
@Order(6)
@Slf4j
public class ControllerTimeIntervalAspect {
	public ControllerTimeIntervalAspect() {
    }

	@SuppressWarnings("unchecked")
	@Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object checkParameter(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		if (args.length<=0)
			return pjp.proceed();

		for(int i=0;i<args.length;i++) {
			Object o = args[i];
			if(o instanceof HttpServletRequest || o instanceof HttpServletResponse
					|| o instanceof MultipartFile) {
				continue;
			}

			if (!(o instanceof Object)||o.getClass()==null||o.getClass().getDeclaredFields()==null){
				continue;
			}

			Map<Integer,Map<String,String>>  timeIntervalMap = new HashMap<>();
			Map<Integer, TimeIntervalValidate> timeIntervalAnnotations = new HashMap<>();

			for (Field field : o.getClass().getDeclaredFields()) {
				if (!field.isAnnotationPresent(TimeIntervalValidate.class))
					continue;

				TimeIntervalValidate validate = field.getAnnotation(TimeIntervalValidate.class);
				field.setAccessible(true);
				String fieldValue = (String) field.get(o);
				timeIntervalMap.putIfAbsent(validate.group(), new HashMap<>());
				if (validate.type() == TimeIntervalFieldEnum.START) {
					timeIntervalMap.get(validate.group()).put("startTime", fieldValue);
				} else if (validate.type() == TimeIntervalFieldEnum.END) {
					timeIntervalMap.get(validate.group()).put("endTime", fieldValue);
				}
				timeIntervalAnnotations.put(validate.group(), validate);
			}

			if (timeIntervalMap.entrySet().size()<=0)
				return pjp.proceed();

			boolean isTimeInterval = false;
			for (Map.Entry<Integer, Map<String, String>> entry : timeIntervalMap.entrySet()) {
				Integer group = entry.getKey();
				Map<String, String> timeMap = entry.getValue();
				String startTimeStr = timeMap.get("startTime");
				String endTimeStr = timeMap.get("endTime");

				TimeIntervalValidate validate = timeIntervalAnnotations.get(group);
				if (validate == null) {
					continue;
				}
				isTimeInterval = TimeUtils.checkIsTimeInterval(isTimeInterval, startTimeStr,
						endTimeStr, validate.intervalType(),validate.interval());
			}

			Assert.isFalse(!isTimeInterval,()->new ServiceException("筛选时间不能同时为空！"));
		}
		return pjp.proceed();
    }
}
