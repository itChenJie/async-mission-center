package com.mission.center.util;


import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mission.center.entity.ResponseWrapper;
import com.mission.center.error.ServerCode;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtils {
    public static final String VALIDATOR_PASS = "PASS";
    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     * @param obj 待校验对象
     */
    public static <T> String validate(T obj){
        Set<ConstraintViolation<T>> validate = validator.validate(obj);
        if (CollectionUtils.isNotEmpty(validate)) {
            Set<String> validatorResultList = new HashSet<>(16);
            validate.forEach(item -> {
                validatorResultList.add(item.getMessage());
            });
            return Joiner.on(",").join(validatorResultList);
        }
        return VALIDATOR_PASS;
    }


    /**
     * 校验对象
     * @param object 待校验对象
     * @param groups 待校验的组
     */
    public static <T> String validateGroups(Object object,Class< ? >... groups){
        Set<ConstraintViolation<Object>> validate = validator.validate(object, groups);
        if (CollectionUtils.isNotEmpty(validate)) {
            Set<String> validatorResultList = new HashSet<>(16);
            validate.forEach(item -> {
                validatorResultList.add(item.getMessage());
            });
            return Joiner.on(",").join(validatorResultList);
        }
        return VALIDATOR_PASS;
    }

    /**
     * 校验参数
     *
     * @param bindingResult
     * @return
     */
    public static ResponseWrapper validate(BindingResult bindingResult) {
        ResponseWrapper baseResponse = new ResponseWrapper();
        if (bindingResult.hasErrors()) {
            List<String> errorMsg = new ArrayList<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMsg.add(fe.getDefaultMessage());
            }

            return ResponseWrapper.fail(ServerCode.PARAM_ILLEGAL_ERROR.getCode(), Joiner.on(",").join(errorMsg));
        }

        return baseResponse;
    }
}
