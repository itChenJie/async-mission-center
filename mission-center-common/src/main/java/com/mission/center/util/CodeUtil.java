package com.mission.center.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Random;

public class CodeUtil {

    public static synchronized String getCode(int type) {
        String time = DateUtil.format(new Date(),"YYYYMMDDhhmm");
        String init = "";
        switch (type) {
            case 0:
                init = "EX";
                break;
            case 1:
                init = "IM";
                break;
            default:
                init ="";
                break;
        }
        return (init + time + random(4)).toUpperCase();
    }

    public static String random(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString().toLowerCase();
    }
}
