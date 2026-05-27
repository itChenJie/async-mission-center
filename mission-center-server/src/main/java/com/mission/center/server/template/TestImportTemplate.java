package com.mission.center.server.template;

import com.mission.center.constant.ServiceModuleEnum;
import com.mission.center.core.bean.DataHandleBean;
import com.mission.center.core.template.impl.AbstractImportTemplate;
import com.mission.center.server.entity.McExportTest;
import com.mission.center.server.mapper.McExportTestMapperDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 导入测试模板
 */
@Component("testImportTemplate")
@RequiredArgsConstructor
public class TestImportTemplate extends AbstractImportTemplate<McExportTest> {

    private final McExportTestMapperDao mapperDao;

    @Override
    public Class<McExportTest> getExcelClass() {
        return McExportTest.class;
    }

    @Override
    public DataHandleBean importData(String taskCode, List<McExportTest> dataList) {
        DataHandleBean handleBean = new DataHandleBean();
        try {
            mapperDao.saveBatch(dataList);
            handleBean.setSuccessNumber((long) dataList.size());
            handleBean.setFailNumber(0L);
        } catch (Exception e) {
            handleBean.setSuccessNumber(0L);
            handleBean.setFailNumber((long) dataList.size());
        }
        return handleBean;
    }

    @Override
    public String name() {
        return "导入测试模板";
    }

    @Override
    public ServiceModuleEnum moduleCode() {
        return ServiceModuleEnum.ERP;
    }
}