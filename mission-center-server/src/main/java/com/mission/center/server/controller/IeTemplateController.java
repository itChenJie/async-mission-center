package com.mission.center.server.controller;


import com.mission.center.entity.ResponseWrapper;
import com.mission.center.server.service.IeTemplateService;
import com.mission.center.server.vo.IeTaskTemplateListRequest;
import com.mission.center.server.vo.IeTaskTemplateResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("template")
@RequiredArgsConstructor
@Api(description = "导入导出任务中心模版管理")
public class IeTemplateController {
    private final IeTemplateService ieTemplateService;

    @ApiOperation(value = "获取模版列表")
    @PostMapping(value = "list")
    public ResponseWrapper<List<IeTaskTemplateResponse>> getTemplateList(@RequestBody @Valid IeTaskTemplateListRequest request){
        List<IeTaskTemplateResponse> response= ieTemplateService.getTemplateList(request);
        return ResponseWrapper.success(response);
    }
}
