package com.mission.center.server.service;

import cn.hutool.core.lang.Assert;
import com.mission.center.core.template.TaskTemplate;
import com.mission.center.error.ServerCode;
import com.mission.center.error.ServiceException;
import com.mission.center.server.vo.IeTaskTemplateListRequest;
import com.mission.center.server.vo.IeTaskTemplateRequest;
import com.mission.center.server.vo.IeTaskTemplateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IeTemplateService {
    private final Map<String, TaskTemplate> taskTemplateMap;

    /**
     * 模版检查
     */
    private void checkTemplateMap() {
        Assert.notNull(taskTemplateMap, ()->new ServiceException(ServerCode.SYSTEM_ERROR.getCode(),"当前系统没有配置导出模板！"));
    }

    /**
     * 获取模版列表
     * @param request
     * @return
     */
    public List<IeTaskTemplateResponse> getTemplateList(IeTaskTemplateListRequest request) {
        checkTemplateMap();

        List<IeTaskTemplateResponse> responses = new ArrayList<>();
        for (Map.Entry<String, TaskTemplate> entry : taskTemplateMap.entrySet()) {
            TaskTemplate template = entry.getValue();
            if (!request.getModuleCode().equals(template.moduleCode()))
                continue;

            responses.add(IeTaskTemplateResponse.builder()
                    .code(entry.getKey())
                    .name(template.name())
                    .build());
        }
        return responses;
    }

    /**
     * 根据模版编码获取
     * @param request
     * @return
     */
    public IeTaskTemplateResponse getTemplate(IeTaskTemplateRequest request) {
        checkTemplateMap();
        TaskTemplate template = taskTemplateMap.get(request.getCode());
        Assert.notNull(template, ()->new ServiceException(ServerCode.PARAM_ERROR.getCode(),"导出模板不存在！"));

        return IeTaskTemplateResponse.builder()
                .code(request.getCode())
                .name(template.name())
                .build();
    }
}
