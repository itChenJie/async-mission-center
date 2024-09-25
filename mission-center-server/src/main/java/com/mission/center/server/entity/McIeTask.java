package com.mission.center.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.mission.center.constant.IeTaskState;
import com.mission.center.constant.IeTaskType;
import com.mission.center.constant.ServiceModuleEnum;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * 任务中心导出任务管理
 */
@Data
@TableName("mc_ie_task")
public class McIeTask implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date completeTime;
    /**
     * 任务编号
     */
    private String code;
    /**
     * 模版编码
     */
    private String templateCode;
    /**
     * 模版名称
     */
    private String templateName;
    /**
     * 模块编码
     */
    @TableField(value = "module_code")
    private ServiceModuleEnum moduleCode;

    /**
     * 菜单编码
     */
    private String menuCode;
    /**
     * 业务模块导出用户唯一标识
     */
    private String serviceModelUserId;
    /**
     * 任务类型0导出 1导入
     */
    private IeTaskType type;
    /**
     * 状态 0待启动 1执行中 2文件待上传 3失败 4成功
     */
    private IeTaskState state;
    /**
     * 总条数
     */
    private Long totalNumber;
    /**
     * 备注
     */
    private String description;
    /**
     * 查询条件json
     */
    private String queryConditionJson;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件服务器key
     */
    private String fileKey;
    /**
     * 进度
     */
    private Integer schedule;
    /**
     * 是否删除 0否 1是
     */
    @TableLogic
    private Integer isDeleted;
    /**
     * 成功数
     */
    private Long successNumber;
    /**
     * 失败数
     */
    private Long failNumber;

    private String importFileKey;

    private String channelCode;
    /**
     * 执行时间
     */
    private Date executionTime;
    /**
     * 执行区间
     */
    private String executionSection;
    /**
     * 当前执行分页数
     */
    private Integer currentPage;
    /**
     * 临时文件保存服务器ip
     */
    private String tempFileSaveIp;
}
