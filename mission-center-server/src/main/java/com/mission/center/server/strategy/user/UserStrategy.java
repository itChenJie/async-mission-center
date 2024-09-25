
package com.mission.center.server.strategy.user;

import cn.hutool.core.lang.Assert;
import com.mission.center.error.ServiceException;
import com.mission.center.server.vo.IeTaskUserListBean;

import java.util.List;
import java.util.Map;

public interface UserStrategy {
    /**
     * 查询用户
     * @param user
     * @return
     */
    public List<IeTaskUserListBean> findUser(String user);

    /**
     * 根据用户标识查询用户信息
     * @param userId
     * @return
     */
    public Map<String,String> userMap(List<String> userId);

    /**
     * 校验 用户是否存在
     * @param o
     */
    default void isExist(Object o){
        Assert.isFalse(o==null,()->new ServiceException("当前账号不存在！"));
    }
}
