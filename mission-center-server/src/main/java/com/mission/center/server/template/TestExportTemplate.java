package com.mission.center.server.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mission.center.constant.ExportTemplateType;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.template.impl.AbstractExportTemplate;
import com.mission.center.server.entity.McExportTest;
import com.mission.center.server.mapper.McExportTestMapper;
import com.mission.center.util.Page;
import com.mission.center.util.PageTotal;
import com.mission.center.util.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 导出测试模板
 * 使用 Alibaba @ExcelProperty 注解
 */
@Component("testExportTemplate")
@RequiredArgsConstructor
public class TestExportTemplate extends AbstractExportTemplate<McExportTest> {

    private final McExportTestMapper mcExportTestMapper;

    @Override
    public ExportTemplateType type() {
        return ExportTemplateType.CUSTOM;
    }

    @Override
    public PageTotal count(String taskCode, String queryConditionJson, Integer shardingSize) {
        Long total = mcExportTestMapper.selectCount(null);
        return PageTotal.of(Math.toIntExact(total), shardingSize);
    }

    @Override
    public Pair<Long, List<McExportTest>> shardingData(TaskRequestContext context, Page page, Long cursorId) {
        LambdaQueryWrapper<McExportTest> wrapper = new LambdaQueryWrapper<>();
        if (cursorId != null && cursorId > 0) {
            wrapper.gt(McExportTest::getId, cursorId);
        }
        wrapper.orderByAsc(McExportTest::getId);
        wrapper.last("LIMIT " + page.getPageSize());

        List<McExportTest> list = mcExportTestMapper.selectList(wrapper);
        Long maxId = list.isEmpty() ? (cursorId != null ? cursorId : 0L) : list.get(list.size() - 1).getId();
        return Pair.of(maxId, list);
    }

    @Override
    public String name() {
        return "导出测试模板";
    }

    @Override
    public ServiceModuleEnum moduleCode() {
        return ServiceModuleEnum.ERP;
    }
}