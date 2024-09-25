package com.mission.center.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 任务中心导出业务模块配置
 */
@Data
@TableName("mc_ie_service_module_config")
public class McIeServiceModuleConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Timestamp createdAt;

    private Timestamp updatedAt;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 模块编号
     */
    private String code;
    /**
     * 备注
     */
    private String description;
    /**
     * 是否删除 0否 1是
     */
    @TableLogic
    private Integer isDeleted;
}
