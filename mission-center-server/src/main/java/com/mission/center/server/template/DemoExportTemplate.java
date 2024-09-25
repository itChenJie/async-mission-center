package com.mission.center.server.template;

import com.mission.center.constant.ExportTemplateType;
import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.executor.excel.ExcelIntensifier;
import com.mission.center.core.executor.excel.WaterMarkExcelIntensifier;
import com.mission.center.core.template.impl.AbstractExportTemplate;
import com.mission.center.util.Page;
import com.mission.center.util.PageTotal;
import com.mission.center.util.Pair;

import java.util.Collections;
import java.util.List;

/**
 * @Description 导出模版 demo
 **/
public class DemoExportTemplate extends AbstractExportTemplate {
    /**
     * 模版类型
     *
     * @return
     */
    @Override
    public ExportTemplateType type() {
        return ExportTemplateType.CUSTOM;
    }

    /**
     * 计算分页信息
     *
     * @param taskCode           任务编码
     * @param queryConditionJson 参数json
     * @param shardingSize       分页大小
     * @return 计算分页信息
     */
    @Override
    public PageTotal count(String taskCode, String queryConditionJson, Integer shardingSize) {
        PageTotal pageTotal = PageTotal.of(Math.toIntExact(100l), shardingSize);
        return pageTotal;
    }

    /**
     * 分页查询
     *
     * @param context  上下文请求
     * @param page     页
     * @param cursorId 当前滚动的分页的游标ID,可以是使用ID 做最大最小值传递。主要是用于优化传递分页查询速度
     * @return 当前最大ID key: cursorId,value: resultList
     */
    @Override
    public Pair<Long, List> shardingData(TaskRequestContext context, Page page, Long cursorId) {
        return Pair.of(cursorId, null);
    }

    /**
     * 模版名称
     *
     * @return
     */
    @Override
    public String name() {
        return "导出演示模版";
    }

    /**
     * 模块编码
     *
     * @return
     */
    @Override
    public ServiceModuleEnum moduleCode() {
        return ServiceModuleEnum.ERP;
    }

    /**
     * 打印水印
     *
     * @return excel增强器
     */
    @Override
    public List<ExcelIntensifier> enhanceExcel() {
        return Collections.singletonList(new WaterMarkExcelIntensifier("导出演示模版 水印"));
    }
}
