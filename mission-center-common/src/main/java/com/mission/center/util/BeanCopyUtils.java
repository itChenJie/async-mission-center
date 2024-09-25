package com.mission.center.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtils {

    public static <T> List<T> copyBeanList(List<?> resourceList, Class<T> target) {
        List<T> targetList = new ArrayList<>();
        if (CollectionUtils.isEmpty((resourceList))) {
            return null;
        }
        resourceList.forEach(item -> {
            T o = null;
            try {
                o = target.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
            BeanUtils.copyProperties(item, o);
            targetList.add(o);
        });
        return targetList;
    }
}
