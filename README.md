# 异步任务中心
## 项目介绍
    异步任务中心，用来处理大量数据的清洗、导出、导入，提供任务平台的能力
### 数据导出
    ExportExecutor 导出执行器
### 数据处理
    CleanseExecutor 数据处理执行器
    每处理一页，记得当前页下标，任务再次启动，从当前页重启
    任务暂停逻辑：
        DataHandleBean.nextExecutionTime 下次执行时间
        当存在执行时间时，任务暂停，设置下次再次启动时间 hangUpTask()
### 数据导入
    ImportExecutor 导入执行器

### 任务执行定时任务 IeTaskJob
    定时执行待执行任务，查询待启动+文件待上传+暂停 并且 执行时间>=当前时间的任务数据
    
#### 执行器公共功能 AbstractCommonTemplate
    更新任务总数 updateTotal(String code,Integer total)
    更新进度 updateProcess(String code,Integer currentPage,Integer totalPage)
    更新进度 updateProcess(String code,Integer currentPage,Integer currentPageInIndex,Integer totalPage)
    完成任务处理 complete(String code,Long successNumber, Long failNumber)
        更新成功失败数量，任务状态
    失败任务处理 fail(String code,String message)
    任务挂起 hangUpTask(String taskCode, Date nextExecutionTime)
#### 数据写入文件
    ExcelExports.writeData()
#### 文件上传oss
    FileService.upload(String path)
#### 任务模版 TaskTemplate
    模版名称  String name();
    模块编码  ServiceModuleEnum moduleCode();
    执行  boolean execute(TaskRequestContext context);
    是否启用导出缓存  default boolean enableExportCache(Object request){return false;}
    任务类型  IeTaskType taskType();
    获取可执行时间（默认当前时间，自定义模版根据业务要求自定义）  default Date executionTime(boolean isFirst){return new Date();}
    可执行区间 24小时制（示例：0-5,20-23）  default String executionSection(){return null;}
    处理完成  default void processingFinish(String taskCode){return ;}

#### 页分任务模版 PageShardingTemplate extends TaskTemplate
    计算分页信息 PageTotal count(String taskCode, String queryConditionJson, Integer shardingSize)
    分页查询 Pair<Long, List<T>> shardingData(TaskRequestContext context, Page page, Long cursorId)