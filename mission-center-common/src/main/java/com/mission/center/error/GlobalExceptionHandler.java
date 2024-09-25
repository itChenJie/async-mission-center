package com.mission.center.error;

import com.mission.center.entity.ResponseWrapper;
import com.mission.center.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获自定义异常信息
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler({ServiceException.class})
    public ResponseWrapper bizExceptionHandler(HttpServletRequest request, ServiceException exception) {
        log.warn("捕获自定义异常信息：{}", exception);
        return ResponseWrapper.fail(exception.getCode(),exception.getMessage());
    }

    /**
     * 捕获参数校验异常
     * @param request
     * @param bindException
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseWrapper methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException bindException) {
        log.warn("捕获参数校验异常信息：{}", bindException);
        return ValidationUtils.validate(bindException.getBindingResult());
    }

    /**
     * 捕获未处理的异常信息
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler
    public ResponseWrapper allExceptionHandler(HttpServletRequest request, Exception exception) {
        log.warn("未知异常信息：{}", exception);
        // TODO 告警
        return ResponseWrapper.fail(ServerCode.SYSTEM_ERROR);
    }
}
