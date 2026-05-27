# 异步任务中心 (Async Mission Center)

## 项目介绍

异步任务中心是一个轻量级的任务调度平台，用于处理大量数据的**清洗、导出、导入**等异步任务。支持多系统接入、分布式部署、任务状态追踪和实时消息推送。

## 核心功能

### 1. 数据导出 (ExportExecutor)
- 支持大数据量分页导出，避免内存溢出
- 导出完成后文件自动上传至 OSS
- 支持自定义导出模版

### 2. 数据处理/清洗 (CleanseExecutor)
- 支持批量数据清洗处理
- 每处理一页记录当前页下标，任务重启后从断点继续
- 支持任务挂起和定时重启

### 3. 数据导入 (ImportExecutor)
- 基于 Alibaba EasyExcel 解析 Excel 文件
- 支持自定义字段转换器
- 支持导入数据校验和批量写入

### 4. 任务调度 (IeTaskJob)
- 定时扫描待执行任务
- 支持执行时间区间限制（如：0-5,20-23）
- 支持任务暂停、重启、失败重试

### 5. 站内信通知 (SSE + Redis Pub/Sub)
- 基于 SSE 实现实时消息推送
- Redis Pub/Sub 支持跨节点消息分发
- 支持同一用户多终端/多标签页同步接收

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.3.2 | 核心框架 |
| MyBatis-Plus | 3.5.2 | ORM 框架 |
| Alibaba EasyExcel | 4.0.3 | Excel 处理 |
| Redis | - | 缓存 + Pub/Sub |
| Swagger | 3.0.0 | API 文档 |
| Hutool | 5.8.11 | 工具类库 |

## 项目结构

```
async-mission-center
├── mission-center-common    # 公共模块：实体、常量、工具类
├── mission-center-core      # 核心模块：执行器、模版抽象、文件服务
├── mission-center-server    # 服务模块：Controller、Service、配置
└── db                       # 数据库脚本
```

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.mission.center</groupId>
    <artifactId>async-mission-center</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 实现任务模版

**导出模版示例：**

```java
@Component("exportUserTemplate")
public class ExportUserTemplate extends PageShardingTemplate<User> {

    @Override
    public Class<User> getExcelClass() {
        return User.class;
    }

    @Override
    public PageTotal count(String taskCode, String queryConditionJson, Integer shardingSize) {
        // 计算分页总数
    }

    @Override
    public Pair<Long, List<User>> shardingData(TaskRequestContext context, Page page, Long cursorId) {
        // 分页查询数据
    }

    @Override
    public String name() {
        return "用户导出";
    }

    @Override
    public ServiceModuleEnum moduleCode() {
        return ServiceModuleEnum.ERP;
    }

    @Override
    public IeTaskType taskType() {
        return IeTaskType.EXPORT;
    }
}
```

**导入模版示例：**

```java
@Component("importUserTemplate")
public class ImportUserTemplate extends AbstractImportTemplate<User> {

    @Override
    public Class<User> getExcelClass() {
        return User.class;
    }

    @Override
    public DataHandleBean importData(String taskCode, List<User> dataList) {
        // 批量写入数据
    }

    @Override
    public String name() {
        return "用户导入";
    }

    @Override
    public ServiceModuleEnum moduleCode() {
        return ServiceModuleEnum.ERP;
    }
}
```

### 3. 自定义字段转换器（导入）

```java
public class StatusConverter implements Converter<Integer> {

    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ...) {
        // Excel 字符串 -> Java Integer
        switch (cellData.getStringValue()) {
            case "待处理": return 0;
            case "处理中": return 1;
            case "已完成": return 2;
            default: return Integer.parseInt(cellData.getStringValue());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ...) {
        // Java Integer -> Excel 字符串
    }
}
```

## SSE 站内信对接

### 后端配置

系统已内置 SSE + Redis Pub/Sub 消息推送机制，任务完成时自动推送通知。

### 前端对接

```javascript
// 建立连接（moduleCode + userId 保证多系统唯一性）
const moduleCode = 'ERP';
const userId = '1001';
const eventSource = new EventSource(`/sse/connect/${moduleCode}/${userId}`);

// 监听连接成功
eventSource.addEventListener('connect', (event) => {
    console.log('SSE 连接成功');
});

// 监听任务通知
eventSource.addEventListener('task-notice', (event) => {
    // 显示通知：您的任务【用户导出】执行成功
    console.log('收到通知:', event.data);
});
```

## 执行器公共功能 (AbstractCommonTemplate)

| 方法 | 说明 |
|------|------|
| `updateTotal(code, total)` | 更新任务总数 |
| `updateProcess(code, currentPage, totalPage)` | 更新进度 |
| `complete(code, successNumber, failNumber)` | 任务完成处理 |
| `fail(code, message)` | 任务失败处理 |
| `hangUpTask(code, nextExecutionTime)` | 任务挂起 |

## 任务模版接口 (TaskTemplate)

| 方法 | 说明 |
|------|------|
| `name()` | 模版名称 |
| `moduleCode()` | 模块编码（多系统隔离） |
| `taskType()` | 任务类型：EXPORT/IMPORT/CLEANSE |
| `execute(context)` | 执行任务逻辑 |
| `executionTime(isFirst)` | 获取可执行时间 |
| `executionSection()` | 执行区间限制（如：0-5,20-23） |

## 任务状态流转

```
WAIT_START → EXECUTING → SUCCESS/FAIL
      ↓
FILE_UPLOADING (导出任务上传文件)
      ↓
STOP (任务暂停)
```

## 分布式部署说明

- 支持 Eureka 注册中心多节点部署
- Redis Pub/Sub 实现跨节点消息分发
- 任务状态通过数据库同步，支持跨节点重启

## License

MIT License