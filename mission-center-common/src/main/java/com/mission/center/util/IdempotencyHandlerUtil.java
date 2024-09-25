package com.mission.center.util;

import com.mission.center.error.ServerCode;
import com.mission.center.error.ServiceException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description
 *  幂等性处理
 **/
public class IdempotencyHandlerUtil {

    /**
     * 对字符串转换成md5 处理 接口短时间内重复点击幂等性文档
     * @param params
     * @return
     */
    public static String generateRequestId(String params) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(params.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ServerCode.PARAM_ERROR.getCode(),"MD5 algorithm not found");
        }
    }
}
