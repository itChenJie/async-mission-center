package com.mission.center.server.service;

import com.mission.center.constants.Constants;
import com.mission.center.server.strategy.user.UserStrategy;
import com.mission.center.server.vo.IeTaskUserListRequest;
import com.mission.center.server.vo.IeTaskUserListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final Map<String, UserStrategy> userStrategyMap;

    /**
     * 查询当前对应系统下用户
     * @param request
     * @return
     */
    public IeTaskUserListResponse findUserList(IeTaskUserListRequest request){
        IeTaskUserListResponse response = new IeTaskUserListResponse() ;
        UserStrategy strategy = userStrategyMap.get(request.getModuleCode().getValue() + Constants.USER_STRATEGY);
        if (strategy==null){
            response.setSupport(false);
            return response;
        }
        response.setList(strategy.findUser(request.getUser()));
        return response;
    }
}
