# CHANGELOG.md

## [1.2.0] - 2026-02-12
### 新增
- 发布 SQL-First 范式文档体系（宣言、对比矩阵、范式标准、白皮书、移植指南）。
- 条件构建器 `BaseCondition` 增强：支持 `add(String sql, boolean condition)` 动态控制片段拼接。
- 增加 `@BusinessAuth` 注解，实现数据权限的无侵入式 AOP 控制。
- 批量操作 `saveBatch`/`replaceBatch` 性能优化，复用 Spring `JdbcTemplate` 原生批处理。

### 改进
- 审计字段填充逻辑优化：`createTime`/`updateTime` 自动填充，`dr` 逻辑删除默认处理。
- 分页方法 `page` 增加智能 COUNT SQL 解析，避免子查询性能问题。
- 工具类 `Sql` 增加 `fill()` 方法，支持打印带参数的完整 SQL，提升调试体验。

### 修复
- 修复 `BaseDao.update` 在更新时误将主键作为更新字段的问题。
- 修复 `SnowflakeId` 在并发极高时序列号溢出问题。

---

## [1.1.0] - 2025-08-01
### 新增
- 引入 `@BusinessAuth` 数据权限注解（实验性）。
- 增加 `BaseDao.updateNull` 方法，支持全字段更新（含 null）。
- 增加 `BaseCondition.mergeParams` 静态方法，方便多条件类参数合并。

### 改进
- 重构 `BaseJdbc`，将分页逻辑抽象为独立方法，便于子类重写。
- 优化 `Sql.countSql` 解析算法，支持更复杂的 SQL（含子查询、CTE）。

### 修复
- 修复 Oracle 数据库下分页 SQL 语法不兼容问题（需手动配置方言）。

---

## [1.0.0] - 2024-05-10
### 初始版本
- 实现核心 `BaseDao`，支持单表 CRUD 零代码开发。
- 双缓存设计：启动时反射解析实体元数据，运行时无反射开销。
- 支持雪花算法、UUID、自增、自定义四种主键策略。
- 审计字段自动填充：`createTime`、`createBy`、`updateTime`、`updateBy`、`dr`。
- 条件构建器 `BaseCondition`，支持 AND/OR/LIKE/IN 等动态条件。
- 原生 SQL 执行器 `BaseJdbc`，提供 `list`/`page`/`row`/`field`/`update` 等方法。
- 内置分页模型 `Page`，统一分页返回格式。
- 集成 Spring 事务管理，无需额外配置。

---