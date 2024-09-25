package com.mission.center.core.confing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mission.center.entity.ResponseWrapper;
import com.mission.center.error.ServerCode;
import com.mission.center.error.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.NDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Order(5)
@Slf4j
public class SystemAspect {

	@Around("@within(org.springframework.web.bind.annotation.RestController)")
    public <T extends Object> T checkParameter(ProceedingJoinPoint pjp) {
		T response = null;
		String requestURI = null;
		Date startTime = new Date();
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		assert sra != null;
		HttpServletRequest request = sra.getRequest();
		try {
			requestURI = request.getRequestURI();
			if (request.getHeader("actionId")!=null){
				String actionId = request.getHeader("actionId");
				NDC.push(actionId);
			}
			Object[] args = pjp.getArgs();
			if (args.length > 0) {
				Map<String, Object> logMap = new HashMap<>();
				for(int i=0;i<args.length;i++) {
					Object o = args[i];
					if(o instanceof HttpServletRequest
							|| o instanceof HttpServletResponse || o instanceof MultipartFile) {
						continue;
					}
					logMap.put("args"+i, o);
				}
				Object logObj = logMap;
				if(logMap.keySet().size() == 1) {
					logObj = logMap.values().stream().findFirst().get();
				}
				log.info("[IP]:{},[API_REQUEST]:{}，[请求参数]：{}"
						,getClientIp(request), requestURI, JSON.toJSONString(logObj, SerializerFeature.WriteMapNullValue));
			}else {
				log.info("[IP]:{},[API_REQUEST]:{}，[请求参数]：{}",getClientIp(request), requestURI);
			}
			response = (T) pjp.proceed();
		} catch (Throwable e) {
			log.info("SystemAspect Error:", e);
			MethodSignature signature = (MethodSignature) pjp.getSignature();
			Method method = signature.getMethod();
			Class<?> claz = method.getReturnType();
			try {
				response = (T) claz.newInstance();
			} catch (InstantiationException e1) {
			} catch (IllegalAccessException e1) {
			}
			if(e instanceof ServiceException) {
				ServiceException se = (ServiceException)e;
				((ResponseWrapper)response).fail(se.getCode(),se.getMessage());
			} else if (e instanceof IllegalArgumentException) {
				((ResponseWrapper)response).fail(ServerCode.PARAM_ERROR.getCode(),e.getMessage());
			} else {
				((ResponseWrapper)response).fail(ServerCode.SYSTEM_ERROR.getCode(),ServerCode.SYSTEM_ERROR.getMsg());
			}
		}

		long t = System.currentTimeMillis() - startTime.getTime();
		log.info("[API_METHOD]:{}, [API_TIME]:{} S, [API_RESPONSE]:{}", requestURI,t / 1000.0,
				JSON.toJSONString(response, SerializerFeature.WriteMapNullValue));
		return response;
    }

	private String getClientIp(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			return xForwardedFor.split(",")[0].trim();
		}
		String xRealIp = request.getHeader("X-Real-IP");
		if (xRealIp != null && !xRealIp.isEmpty()) {
			return xRealIp;
		}
		return request.getRemoteAddr();
	}
}
