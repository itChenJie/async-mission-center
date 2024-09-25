package com.mission.center.core.template.impl;

import com.mission.center.constant.ExportTemplateType;
import com.mission.center.constant.IeTaskState;
import com.mission.center.core.bean.TaskRequestContext;
import com.mission.center.core.bean.UpdateNumberBean;
import com.mission.center.core.bean.UpdateStateBean;
import com.mission.center.core.executor.excel.ExcelIntensifierExecutor;
import com.mission.center.core.template.IExportTemplate;
import com.mission.center.core.utils.ExcelBeanUtils;
import com.mission.center.error.ServiceException;
import com.mission.center.excel.ExcelBean;
import com.mission.center.excel.ExcelExports;
import com.mission.center.excel.annotations.McExcelProperty;
import com.mission.center.excel.bean.ExcelFiled;
import com.mission.center.util.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.groups.Default;
import java.util.List;
import java.util.Objects;
/**
 * 导出模版执行器 基类
 * @param <T>
 */
@Slf4j
public abstract class AbstractExportTemplate<T> extends AbstractCommonTemplate implements IExportTemplate<T>, ExcelIntensifierExecutor {

    public String sheetPrefix() {
        return ExcelBean.DEFAULT_SHEET_GROUP;
    }

    /**
     * 导出模板类分组 {@link McExcelProperty#group()}
     *
     * @param context context
     * @return export class group
     */
    public Class<?>[] exportGroup(TaskRequestContext context) {
        return new Class<?>[]{Default.class};
    }

    /**
     * 导出
     *
     * @param context
     * @return
     */
    @Override
    public boolean execute(TaskRequestContext context) {
        iIeTaskService = context.getIIeTaskService();
        try (ExcelBean excelBean = ExcelExports.createWorkbook()){
            PageTotal pageTotal = count(context.getTaskCode(),context.getQueryConditionJson(),context.getShardingSize());
            ExportTemplateType templateType = this.type();
            List<ExcelFiled> fieldList = null;
            switch (templateType) {
                case CUSTOM:
                    fieldList = ExcelBeanUtils.getExcelFileAnalyze(GenericUtils.getClassT(this, 0)
                            ,exportGroup(context));
                    break;
                case DYNAMIC:
                    fieldList = ExcelBeanUtils.getDynamicExcelFiledAnalyze(getDynamicsTitles(context.getQueryConditionJson()));
                    break;
                default:
                    throw new ServiceException("当前模版类型不支持！"+templateType.getValue());
            }
            updateTotal(context.getTaskCode(),pageTotal.getTotal());
            if (pageTotal.getTotal()<=0) {
                ExcelExports.writeHeader(excelBean, fieldList, sheetPrefix());
                ExcelExports.writeWorkbook(excelBean, context.getOut());
                return true;
            }

            int totalPage = PageUtil.totalPage(pageTotal.getTotal(), pageTotal.getPageSize());
            Page page = new Page(1, pageTotal.getPageSize(), pageTotal.getTotal());
            Long cursorId = 0L;
            Long number = 0l;
            for (int i = 0; i < totalPage; i++) {
                page.setPageNum(i + 1);
                Pair<Long, List<T>> pair  = shardingData(context, page, cursorId);
                if (Objects.nonNull(pair)) {
                    cursorId = pair.getKey();
                    switch (templateType) {
                        case CUSTOM:
                            ExcelExports.writeData(excelBean, fieldList, pair.getValue(), sheetPrefix());
                            break;
                        case DYNAMIC:
                            ExcelExports.writeDynamicData(excelBean, fieldList, pair.getValue(), sheetPrefix());
                            break;
                    }
                }
                updateProcess(context.getTaskCode(),i,totalPage);
                number+=pair.getValue().size();
            }
            iIeTaskService.updateNumber(context.getTaskCode(),new UpdateNumberBean(number,null));
            excelBean.logExportInfo(log);
            this.executeEnhance(excelBean.getWorkbook(), context);
            ExcelExports.writeWorkbook(excelBean, context.getOut());
            iIeTaskService.updateState(context.getTaskCode(), new UpdateStateBean(IeTaskState.FILE_UPLOADING));
        }catch (Exception e) {
            e.printStackTrace();
            fail(context.getTaskCode(),e.getMessage());
            return false;
        }
        return true;
    }
}
