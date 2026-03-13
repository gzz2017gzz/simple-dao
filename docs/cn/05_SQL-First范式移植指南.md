# 05 SQL-First范式移植指南
## 200行代码，把你的语言从ORM中解放出来

版本：1.0  
发布日期：2026年2月12日  
目标读者：Python/Node/Go/PHP/.NET/Rust 开发者  
阅读时长：15分钟  
实现时长：2小时（有AI）～ 2天（纯手工）

---

## 零、为什么你可以信任这份指南

这份指南背后的 SimpleDAO 已在 Java/Spring 生态的生产环境中稳定运行 **3年+**，支撑日均百万级请求，覆盖复杂业务场景。  
**2000行 Java 代码验证过的范式，可以在你的语言里用 200 行重新实现。**

---

### 核心设计目标
当你实现自己的 SQL-First 数据访问层时，请始终牢记：
- **业务代码浓度越高越好**，框架代码越少越好。
- **框架存在感越低越好**，让开发者感觉不到框架的存在。
- **扩展必须白盒**：任何扩展功能都应在业务层用语言原生方式实现，而不是深入框架底层。
- **不制造错误、不添乱**：框架只做样板代码的消除者和数据库错误的传递者。

---

## 第一部分：核心抽象（语言无关）

任何语言的 SQL-First 数据访问层，都由以下 **4个核心抽象** 构成。

### 1.1 实体元数据收集器
**职责**：读取实体类/结构体的注解/标签，解析出表名、字段名、主键名、主键策略。**启动时执行，缓存结果，运行时零反射。**

**各语言实现要点**：
| 语言 | 元数据机制 | 缓存时机 |
|------|----------|--------|
| Java | 注解 + 反射 | 静态构造器 |
| Python | 类属性/装饰器 | 模块加载时 |
| Node/TS | 装饰器 + Reflect | 模块加载时 |
| Go | 结构体标签 + reflect | init() |
| PHP | 注解/属性 | 构造函数 |
| .NET | Attribute + Reflection | 静态构造器 |
| Rust | 派生宏 + 过程宏 | 编译时 |

### 1.2 BaseDao<T>
**职责**：为实体 T 提供继承即得的单表 CRUD 方法。

**最小方法集**：save/update/updateNull/delete/findById/saveBatch/updateBatch/deleteBatch/list/page/count/exists

**实现要点**：所有方法基于 **原生 SQL 字符串 + 参数数组** 实现，不封装复杂的 QueryBuilder。

### 1.3 Condition 构建器
**职责**：拼接 SQL WHERE 子句 + 收集参数。这是 SQL-First 范式的灵魂。

**核心 API**：
- add(String sql)  
- add(String sql, boolean condition)  
- add(String sql, Object value)  
- add(String sql, String value, int site)  // site: 1左%, 2右%, 3全%  
- add(String sql, Object[] values)

**输出**：`where()` 返回 WHERE 子句，`array()` 返回参数数组。

### 1.4 原生SQL执行器
**职责**：执行开发者手写的完整 SQL，映射结果。

**最小方法集**：list/page/row/field/update/batch

**实现要点**：直接使用语言原生的预编译执行 API，结果映射约定优于配置（驼峰/下划线自动转换），分页先 count 再 limit。

---

## 第二部分：各语言实现要点（速查表）

### 🐍 Python（难度：★☆☆）
- JDBC 等价物：sqlite3、psycopg2、PyMySQL  
- 优势：动态语言，装饰器成熟  
- 推荐项目名：pydao、simple-db、sqlfirst

### 📦 Node.js / TypeScript（难度：★☆☆）
- JDBC 等价物：mysql2、pg  
- 优势：TypeScript 装饰器 + Reflect 元数据  
- 推荐项目名：sqlex、type-dao、node-sql-first

### 🐹 Go（难度：★★☆）
- JDBC 等价物：database/sql  
- 挑战：无继承，反射语法稍繁琐，泛型较弱  
- 推荐项目名：gosqlc、dao-go、lightdb

### 🐘 PHP（难度：★☆☆）
- JDBC 等价物：PDO  
- 优势：动态特性丰富，PHP 8+ 注解完善  
- 推荐项目名：pdo-dao、simple-db、sql-first-php

### 🎯 .NET / C#（难度：★☆☆）
- JDBC 等价物：Dapper（本身就是 SQL-First 风格）  
- 优势：Dapper 已是扩展方法，缺的是 BaseDao 和 Condition 层  
- 推荐项目名：Dapper.Simple、DapperDao

### 🦀 Rust（难度：★★☆）
- JDBC 等价物：sqlx  
- 优势：社区偏向 SQL-First，过程宏可实现优雅映射  
- 推荐项目名：sqlx-dao、sea-simple

---

## 第三部分：从“思想种子”到“真实代码”

### 3.1 最小可行性标准（必过项）
1. 单表 CRUD：继承/组合 BaseDao，空类可用  
2. 条件构建器：支持 and/or/LIKE/IN，参数与 SQL 分离  
3. 原生 SQL 执行：手写 JOIN 查询，自动映射到 VO  
4. 分页：统一 page 方法，自动 count  
5. 审计字段：create_time/update_time/dr 自动填充  
6. 无自造错误：不封装框架专有异常  
7. **扩展白盒**：任何扩展功能都应在业务层实现，不依赖框架底层拦截

### 3.2 从0到1的4小时路线图
- 第1小时：实现 EntityMeta 收集器 + Condition 构建器  
- 第2小时：实现 BaseDao.save/findById/update/delete  
- 第3小时：实现原生 SQL 执行器（list/page/row）  
- 第4小时：跑通一个真实业务（用户+部门联表查询）

### 3.3 AI 提示词模板（以 Python 为例）
```
请根据以下 Java 版的 SimpleDAO 核心代码，帮我用 Python 实现一个最小化的 SQL-First 数据访问层。
要求：
- 使用 sqlite3 作为底层驱动
- 实现 EntityMeta 收集器、BaseDao、Condition 构建器
- 支持单表 CRUD 和条件查询
- 遵循“业务代码浓度高、框架存在感低”的原则
- 输出代码和简单使用示例

Java 源码（略）
```

---

## 第四部分：传播——让你的实现被看见

### 4.1 命名建议
公式：`[语言特征] + [Dao/DB/SQL] + [轻量后缀]`  
示例：pydao、node-sql-first、gosqlc、dapper.simple、sqlx-dao

**在 README 第一行致敬**：  
> Inspired by [SimpleDAO](https://gitee.com/...) - A SQL-First data access paradigm from Java ecosystem.

### 4.2 必须包含的三份文档
1. **README.md**：5分钟上手示例（用户+部门联表）  
2. **MANIFESTO.md**：SQL-First 宣言的核心摘要  
3. **BENCHMARK.md**：与主流 ORM 的代码行数/心智负担对比

---

## 终章：这200行代码的意义

**你即将写的这200行代码，不是为了替代你语言的ORM。是为了证明：在你的语言里，ORM不是必须的。**

当第一个 Python 开发者看到 `pydao` 的 README，发现 200 行代码就能摆脱 SQLAlchemy 的复杂继承树时——**他会意识到：过去三年，他不是在学习，而是在被消耗。**

**你会成为那个递钥匙的人。**

---

**移植指南完。**

> **相关文档**  
> - 想了解 SQL-First 的思想原点？请移步 **[01 SQL-First宣言](#)**  
> - 想看到 SQL-First 与 ORM 的全面对比？请移步 **[02 全场景对比矩阵](#)**  
> - 想了解 SQL-First 范式的具体实现标准？请移步 **[03 SQL-First 持久层开发范式标准](#)**  
> - 想看到 Java 版的实际落地案例？请移步 **[04 SimpleDAO技术白皮书](#)**

---

*本文档采用 CC BY 4.0 许可。你可以自由复制、分发、改写，只需注明来源。*  
*最好的致敬，是在你的语言里写一个自己的 SimpleDAO。然后告诉下一个人：你也可以。*

---

以上为融入新理念后的五份文档。如需进一步调整，请随时提出。