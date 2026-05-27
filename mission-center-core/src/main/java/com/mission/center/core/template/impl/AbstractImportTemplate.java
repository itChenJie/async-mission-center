package com.mission.center.core.template.impl;

import com.mission.center.constant.IeTaskType;
import com.mission.center.core.bean.DataHandleBean;
import com.mission.center.core.template.TaskTemplate;

import java.util.List;

/**
 * 导入模板基类
 * @param <T> Excel实体类类型
 */
public abstract class AbstractImportTemplate<T> extends AbstractCommonTemplate implements TaskTemplate {

    /**
     * 获取对应绑定的 Excel 实体类
     */
    public abstract Class<T> getExcelClass();

    /**
     * 批量导入数据处理逻辑 (由业务方实现)
     * @param taskCode 任务编码
     * @param dataList 当前批次解析出来的数据
     * @return 包含当前批次成功/失败数量的对象
     */
    public abstract DataHandleBean importData(String taskCode, List<T> dataList);

    @Override
    public IeTaskType taskType() {
        return IeTaskType.IMPORT;
    }

    @Override
    public boolean execute(com.mission.center.core.bean.TaskRequestContext context) {
        // 导入模板的 execute 不直接使用，由 ImportExecutor 调用 importData
        return true;
    }
}